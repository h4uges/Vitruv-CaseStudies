package mir.responses.responses5_1ToJava.pcm2java;

import edu.kit.ipd.sdq.vitruvius.dsls.response.runtime.AbstractResponseRealization;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.interfaces.UserInteracting;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.EChange;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.feature.reference.containment.DeleteNonRootEObjectInList;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;

@SuppressWarnings("all")
class RemovedParameterResponse extends AbstractResponseRealization {
  public RemovedParameterResponse(final UserInteracting userInteracting) {
    super(userInteracting);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return DeleteNonRootEObjectInList.class;
  }
  
  private boolean checkChangeProperties(final DeleteNonRootEObjectInList<Parameter> change) {
    EObject changedElement = change.getOldAffectedEObject();
    // Check model element type
    if (!(changedElement instanceof OperationSignature)) {
    	return false;
    }
    
    // Check feature
    if (!change.getAffectedFeature().getName().equals("parameters__OperationSignature")) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof DeleteNonRootEObjectInList<?>)) {
    	return false;
    }
    DeleteNonRootEObjectInList typedChange = (DeleteNonRootEObjectInList)change;
    if (!checkChangeProperties(typedChange)) {
    	return false;
    }
    getLogger().debug("Passed precondition check of response " + this.getClass().getName());
    return true;
  }
  
  public void executeResponse(final EChange change) {
    DeleteNonRootEObjectInList<Parameter> typedChange = (DeleteNonRootEObjectInList<Parameter>)change;
    final org.palladiosimulator.pcm.repository.Parameter oldValue = typedChange.getOldValue();
    if (oldValue != null) {
    	typedChange.setOldValue(new mir.responses.mocks.org.palladiosimulator.pcm.repository.ParameterContainerMock(oldValue, typedChange.getOldAffectedEObject()));
    }
    mir.routines.pcm2java.RemovedParameterEffect effect = new mir.routines.pcm2java.RemovedParameterEffect(this.executionState, this);
    effect.setChange(typedChange);
    effect.applyEffect();
  }
}
