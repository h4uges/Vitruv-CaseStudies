package edu.kit.ipd.sdq.vitruvius.tests.framework.run.SynchronisationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.impl.ClassImpl;
import org.emftext.language.java.classifiers.impl.InterfaceImpl;
import org.emftext.language.java.containers.CompilationUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;
import de.uka.ipd.sdq.pcm.util.PcmResourceFactoryImpl;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.Change;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.FileChange;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.FileChange.FileChangeKind;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.ModelInstance;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.VURI;
import edu.kit.ipd.sdq.vitruvius.framework.metarepository.MetaRepositoryImpl;
import edu.kit.ipd.sdq.vitruvius.framework.run.editor.monitored.emf.MonitoredEmfEditorImpl;
import edu.kit.ipd.sdq.vitruvius.framework.run.propagationengine.EMFModelPropagationEngineImpl;
import edu.kit.ipd.sdq.vitruvius.framework.run.syncmanager.SyncManagerImpl;
import edu.kit.ipd.sdq.vitruvius.framework.synctransprovider.TransformationExecutingProvidingImpl;
import edu.kit.ipd.sdq.vitruvius.framework.vsum.VSUMImpl;
import edu.kit.ipd.sdq.vitruvius.tests.jamopppcm.util.JaMoPPPCMTestUtil;
import edu.kit.ipd.sdq.vitruvius.tests.util.TestUtil;

public class SynchronisationTest {

    private static final Logger logger = Logger.getLogger(SynchronisationTest.class.getSimpleName());

    private static final String MODEL_PATH = "testModels";
    private static final String PCM_REPOSITORY_FILE_NAME = MODEL_PATH + "/" + "pcmRepoTest.repository";
    private static final String PCM_REPOSITORY_NAME = "testRepository";
    private static final String PCM_INTERFACE_CREATION_NAME = "TestCreateInterface";
    private static final String PCM_REPOSITORY_CREATION_NAME = "TestCreateComponent";
    private static final String PCM_INTERFACE_RENAME_NAME = "TestRenameInterface";

    private MonitoredEmfEditorImpl monitor;
    private SyncManagerImpl syncManager;

    private VURI sourceModelURI;
    private Repository repository;

    /**
     * Set up SyncMangaer and metaRepository facility. The method also creates the repository and
     * synchronizes the file change.
     *
     * @throws Exception
     */
    @Before
    public void setUpTest() throws Exception {
        // set up syncManager, monitor and metaRepostitory
        final MetaRepositoryImpl metaRepository = JaMoPPPCMTestUtil.createJaMoPPPCMMetaRepository();
        final VSUMImpl vsum = TestUtil.createVSUM(metaRepository);
        final TransformationExecutingProvidingImpl syncTransformationProvider = new TransformationExecutingProvidingImpl();
        final EMFModelPropagationEngineImpl propagatingChange = new EMFModelPropagationEngineImpl(
                syncTransformationProvider);
        this.syncManager = new SyncManagerImpl(vsum, propagatingChange, vsum, metaRepository, vsum);
        this.monitor = new MonitoredEmfEditorImpl(this.syncManager, this.syncManager.getModelProviding());

        // create pcm model instance
        this.repository = RepositoryFactory.eINSTANCE.createRepository();
        this.repository.setEntityName(PCM_REPOSITORY_NAME);
        this.sourceModelURI = VURI.getInstance(TestUtil.PROJECT_URI + "/" + PCM_REPOSITORY_FILE_NAME);

        final ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("repository", new PcmResourceFactoryImpl());
        final Resource resource = resourceSet.createResource(this.sourceModelURI.getEMFUri());
        if (null == resource) {
            fail("Could not create resource with URI: " + this.sourceModelURI);
            return;
        }
        resource.getContents().add(this.repository);
        final Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        try {
            resource.save(options);
        } catch (final IOException e) {
            logger.warn("createRepositoryFile failed: Could not save resource: " + resource.getURI() + ". Exception: "
                    + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        this.createAndSyncFileChange(this.sourceModelURI);
        this.addContentAdapter(this.repository);
    }

    /**
     * Test watcher that moves src files (which are created during the previous test) to a own
     * folder
     */
    @Rule
    public TestWatcher watchmen = new TestWatcher() {
        @Override
        protected void finished(final org.junit.runner.Description description) {
            final String previousMethodName = description.getMethodName();
            TestUtil.moveSrcFilesFromMockupProjectToPathWithTimestamp(previousMethodName);
            TestUtil.moveVSUMProjectToOwnFolder(previousMethodName);
        };
    };

    /**
     * Triggers the synchronisation of the model. Should synchronize the creation of the interface.
     */
    @Test
    public void testSyncInterface() {
        createInterface(this.repository);

        this.monitor.triggerSynchronisation(this.sourceModelURI);

        // a java interface in the package "testRepository" should have been created
        final String javaInterfaceLocation = this.getSrcPath() + PCM_REPOSITORY_NAME + "/"
                + PCM_INTERFACE_CREATION_NAME + ".java";
        final VURI interfaceVURI = VURI.getInstance(javaInterfaceLocation);
        final ModelInstance newInterface = this.syncManager.getModelProviding().getAndLoadModelInstanceOriginal(
                interfaceVURI);
        final CompilationUnit cu = newInterface.getUniqueRootEObjectIfCorrectlyTyped(CompilationUnit.class);
        this.assertClassifierInJaMoPPCompilationUnit(cu, InterfaceImpl.class, PCM_INTERFACE_CREATION_NAME, true);
    }

    @Test
    public void testSyncBasicComponent() {
        this.createBasicComponent(this.repository);
        this.monitor.triggerSynchronisation(this.sourceModelURI);

        // a package and an interface should have been created in the package "testRepository
        final String javaClassLocation = this.getSrcPath() + PCM_REPOSITORY_NAME + "/" + PCM_REPOSITORY_CREATION_NAME
                + "/" + PCM_REPOSITORY_CREATION_NAME + "Impl" + ".java";
        final VURI vuri = VURI.getInstance(javaClassLocation);
        final ModelInstance newClass = this.syncManager.getModelProviding().getAndLoadModelInstanceOriginal(vuri);
        final CompilationUnit cu = newClass.getUniqueRootEObjectIfCorrectlyTyped(CompilationUnit.class);
        final Classifier classifier = this.assertClassifierInJaMoPPCompilationUnit(cu, ClassImpl.class,
                PCM_REPOSITORY_CREATION_NAME + "Impl", true);
        if (null != classifier) {
            this.assertNamespaceOfClassifier(classifier, PCM_REPOSITORY_NAME + "." + PCM_REPOSITORY_CREATION_NAME);
        }
    }

    @Test
    public void testRenameInterface() {
        final OperationInterface oi = createInterface(this.repository);
        this.monitor.triggerSynchronisation(this.sourceModelURI);

        oi.setEntityName(PCM_INTERFACE_RENAME_NAME);
        this.monitor.triggerSynchronisation(this.sourceModelURI);

        // the compilaition unit should now have another name and the should also contain the
        // interface with the new name
        final String newInterfaceLocation = this.getSrcPath() + PCM_REPOSITORY_NAME + "/" + PCM_INTERFACE_RENAME_NAME
                + ".java";
        final VURI newInterfaceLocationVURI = VURI.getInstance(newInterfaceLocation);
        final ModelInstance renamedInterface = this.syncManager.getModelProviding().getAndLoadModelInstanceOriginal(
                newInterfaceLocationVURI);
        final CompilationUnit cu = renamedInterface.getUniqueRootEObjectIfCorrectlyTyped(CompilationUnit.class);
        final Classifier classifier = this.assertClassifierInJaMoPPCompilationUnit(cu, InterfaceImpl.class,
                PCM_INTERFACE_RENAME_NAME, true);
        logger.info("found classifier: " + classifier);
    }

    private void assertNamespaceOfClassifier(final Classifier classifier, final String expectedNamespace) {
        String namespace = classifier.getContainingCompilationUnit().getNamespacesAsString();
        if (namespace.endsWith(".") || namespace.endsWith("$")) {
            namespace = namespace.substring(0, namespace.length() - 1);
        }
        assertEquals("Namespace of classifier not equal expected namespace", expectedNamespace, namespace);
    }

    private void createBasicComponent(final Repository repository) {
        final BasicComponent bc = RepositoryFactory.eINSTANCE.createBasicComponent();
        bc.setEntityName(PCM_REPOSITORY_CREATION_NAME);
        bc.setRepository__RepositoryComponent(repository);
    }

    private String getSrcPath() {
        return TestUtil.PROJECT_URI + "/src/";
    }

    private Classifier assertClassifierInJaMoPPCompilationUnit(final CompilationUnit cu, final Class<?> classifierType,
            final String classifierName, final boolean failIfNotFound) {
        for (final Classifier classifier : cu.getClassifiers()) {
            if (classifier.getClass().equals(classifierType) && classifier.getName().equals(classifierName)) {
                logger.debug("Found classifier with type " + classifierType.getSimpleName() + " and name "
                        + classifierName + " in CompilationUnit " + cu.getName());
                return classifier;
            }
        }
        if (failIfNotFound) {
            fail("Classifier with type " + classifierType.getSimpleName() + "  and name " + classifierName
                    + " not found in CompilationUnit " + cu.getName());
        }
        return null;
    }

    /**
     * create an OperaitonInterface
     *
     * @param repository
     * @return
     */
    private static OperationInterface createInterface(final Repository repository) {
        final OperationInterface opInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
        opInterface.setEntityName(PCM_INTERFACE_CREATION_NAME);
        opInterface.setRepository__Interface(repository);
        return opInterface;
    }

    /**
     * add monitor to root object of new file
     *
     * @param resource
     *            resource containing the root EObject of the new file
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void addContentAdapter(final EObject eObject) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        final Field f = this.monitor.getClass().getDeclaredField("emfMonitorAdapter");
        f.setAccessible(true);
        final EContentAdapter contentAdapter = (EContentAdapter) f.get(this.monitor);
        eObject.eAdapters().add(contentAdapter);
    }

    /**
     * synchronize file create, which internally creates a emf change for creation of root object in
     * repository model
     */
    private void createAndSyncFileChange(final VURI vuri) {
        final FileChange fileChange = new FileChange(FileChangeKind.CREATE, vuri);
        final List<Change> changes = new ArrayList<Change>(1);
        changes.add(fileChange);
        this.syncManager.synchronizeChanges(changes);
    }
}