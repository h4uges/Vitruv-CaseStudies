package edu.kit.ipd.sdq.vitruvius.tests.casestudies.jmljava.unittests.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import edu.kit.ipd.sdq.vitruvius.framework.metamodel.ModelInstance;
import edu.kit.ipd.sdq.vitruvius.framework.tuid.TUID;
import edu.kit.ipd.sdq.vitruvius.framework.util.command.VitruviusRecordingCommand;
import edu.kit.ipd.sdq.vitruvius.framework.util.datatypes.VURI;
import edu.kit.ipd.sdq.vitruvius.framework.metamodel.ModelProviding;

public class ModelProvidingMock implements ModelProviding {
    final Map<VURI, ModelInstance> mapping = new HashMap<VURI, ModelInstance>();

    public void add(final ModelInstance model) {
        this.mapping.put(model.getURI(), model);
    }

    @Override
    public ModelInstance getAndLoadModelInstanceOriginal(final VURI uri) {
        return this.mapping.get(uri);
    }

    @Override
    public void saveExistingModelInstanceOriginal(final VURI vuri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachTransactionalEditingDomain() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteModelInstanceOriginal(final VURI vuri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveModelInstanceOriginalWithEObjectAsOnlyContent(final VURI vuri, final EObject rootEObject,
            final TUID oldTUID) {
        throw new UnsupportedOperationException();
    }

	@Override
	public void forceReloadModelInstanceOriginalIfExisting(VURI modelURI) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void createRecordingCommandAndExecuteCommandOnTransactionalDomain(Callable<Void> callable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeRecordingCommandOnTransactionalDomain(VitruviusRecordingCommand command) {
		// TODO Auto-generated method stub
		
	}
}
