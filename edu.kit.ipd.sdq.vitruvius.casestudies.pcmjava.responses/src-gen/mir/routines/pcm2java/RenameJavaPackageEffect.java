package mir.routines.pcm2java;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.responses.pcm2java.Pcm2JavaHelper;
import edu.kit.ipd.sdq.vitruvius.dsls.response.runtime.AbstractEffectRealization;
import edu.kit.ipd.sdq.vitruvius.dsls.response.runtime.ResponseExecutionState;
import edu.kit.ipd.sdq.vitruvius.dsls.response.runtime.structure.CallHierarchyHaving;
import java.io.IOException;
import mir.routines.pcm2java.RoutinesFacade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.core.entity.NamedElement;

@SuppressWarnings("all")
public class RenameJavaPackageEffect extends AbstractEffectRealization {
  public RenameJavaPackageEffect(final ResponseExecutionState responseExecutionState, final CallHierarchyHaving calledBy) {
    super(responseExecutionState, calledBy);
  }
  
  private NamedElement sourceElementMappedToPackage;
  
  private org.emftext.language.java.containers.Package parentPackage;
  
  private String packageName;
  
  private String expectedTag;
  
  private boolean isSourceElementMappedToPackageSet;
  
  private boolean isParentPackageSet;
  
  private boolean isPackageNameSet;
  
  private boolean isExpectedTagSet;
  
  public void setSourceElementMappedToPackage(final NamedElement sourceElementMappedToPackage) {
    this.sourceElementMappedToPackage = sourceElementMappedToPackage;
    this.isSourceElementMappedToPackageSet = true;
  }
  
  public void setParentPackage(final org.emftext.language.java.containers.Package parentPackage) {
    this.parentPackage = parentPackage;
    this.isParentPackageSet = true;
  }
  
  public void setPackageName(final String packageName) {
    this.packageName = packageName;
    this.isPackageNameSet = true;
  }
  
  public void setExpectedTag(final String expectedTag) {
    this.expectedTag = expectedTag;
    this.isExpectedTagSet = true;
  }
  
  public boolean allParametersSet() {
    return isSourceElementMappedToPackageSet&&isParentPackageSet&&isPackageNameSet&&isExpectedTagSet;
  }
  
  private String getRetrieveTag0(final NamedElement sourceElementMappedToPackage, final org.emftext.language.java.containers.Package parentPackage, final String packageName, final String expectedTag) {
    return expectedTag;
  }
  
  private EObject getCorrepondenceSourceJavaPackage(final NamedElement sourceElementMappedToPackage, final org.emftext.language.java.containers.Package parentPackage, final String packageName, final String expectedTag) {
    return sourceElementMappedToPackage;
  }
  
  protected void executeEffect() throws IOException {
    getLogger().debug("Called routine RenameJavaPackageEffect with input:");
    getLogger().debug("   NamedElement: " + this.sourceElementMappedToPackage);
    getLogger().debug("   Package: " + this.parentPackage);
    getLogger().debug("   String: " + this.packageName);
    getLogger().debug("   String: " + this.expectedTag);
    
    org.emftext.language.java.containers.Package javaPackage = initializeRetrieveElementState(
    	() -> getCorrepondenceSourceJavaPackage(sourceElementMappedToPackage, parentPackage, packageName, expectedTag), // correspondence source supplier
    	(org.emftext.language.java.containers.Package _element) -> true, // correspondence precondition checker
    	() -> getRetrieveTag0(sourceElementMappedToPackage, parentPackage, packageName, expectedTag), // tag supplier
    	org.emftext.language.java.containers.Package.class,
    	false, true, false);
    if (isAborted()) {
    	return;
    }
    
    preProcessElements();
    new mir.routines.pcm2java.RenameJavaPackageEffect.EffectUserExecution(getExecutionState(), this).executeUserOperations(
    	sourceElementMappedToPackage, parentPackage, packageName, expectedTag, javaPackage);
    postProcessElements();
  }
  
  private static class EffectUserExecution extends AbstractEffectRealization.UserExecution {
    @Extension
    private RoutinesFacade effectFacade;
    
    public EffectUserExecution(final ResponseExecutionState responseExecutionState, final CallHierarchyHaving calledBy) {
      super(responseExecutionState);
      this.effectFacade = new RoutinesFacade(responseExecutionState, calledBy);
    }
    
    private void executeUserOperations(final NamedElement sourceElementMappedToPackage, final org.emftext.language.java.containers.Package parentPackage, final String packageName, final String expectedTag, final org.emftext.language.java.containers.Package javaPackage) {
      EList<String> _namespaces = javaPackage.getNamespaces();
      _namespaces.clear();
      boolean _notEquals = (!Objects.equal(parentPackage, null));
      if (_notEquals) {
        EList<String> _namespaces_1 = javaPackage.getNamespaces();
        EList<String> _namespaces_2 = parentPackage.getNamespaces();
        Iterables.<String>addAll(_namespaces_1, _namespaces_2);
        EList<String> _namespaces_3 = javaPackage.getNamespaces();
        String _name = parentPackage.getName();
        _namespaces_3.add(_name);
      }
      javaPackage.setName(packageName);
      String _buildJavaFilePath = Pcm2JavaHelper.buildJavaFilePath(javaPackage);
      this.persistProjectRelative(sourceElementMappedToPackage, javaPackage, _buildJavaFilePath);
    }
  }
}