package mir.reactions.reactionsJavaToPcm.java2pcm;

import mir.routines.java2pcm.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;
import tools.vitruv.framework.userinteraction.UserInteracting;

@SuppressWarnings("all")
class CreateClassReaction extends AbstractReactionRealization {
  public CreateClassReaction(final UserInteracting userInteracting) {
    super(userInteracting);
  }
  
  public void executeReaction(final EChange change) {
    InsertEReference<org.emftext.language.java.containers.Package, org.emftext.language.java.classifiers.Class> typedChange = (InsertEReference<org.emftext.language.java.containers.Package, org.emftext.language.java.classifiers.Class>)change;
    org.emftext.language.java.containers.Package affectedEObject = typedChange.getAffectedEObject();
    org.emftext.language.java.classifiers.Class newValue = typedChange.getNewValue();
    mir.routines.java2pcm.RoutinesFacade routinesFacade = new mir.routines.java2pcm.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsJavaToPcm.java2pcm.CreateClassReaction.ActionUserExecution userExecution = new mir.reactions.reactionsJavaToPcm.java2pcm.CreateClassReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(affectedEObject, newValue, routinesFacade);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return InsertEReference.class;
  }
  
  private boolean checkChangeProperties(final EChange change) {
    InsertEReference<org.emftext.language.java.containers.Package, org.emftext.language.java.classifiers.Class> relevantChange = (InsertEReference<org.emftext.language.java.containers.Package, org.emftext.language.java.classifiers.Class>)change;
    if (!(relevantChange.getAffectedEObject() instanceof org.emftext.language.java.containers.Package)) {
    	return false;
    }
    if (!(relevantChange.getNewValue() instanceof org.emftext.language.java.classifiers.Class)) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof InsertEReference)) {
    	return false;
    }
    getLogger().debug("Passed change type check of reaction " + this.getClass().getName());
    if (!checkChangeProperties(change)) {
    	return false;
    }
    getLogger().debug("Passed change properties check of reaction " + this.getClass().getName());
    getLogger().debug("Passed complete precondition check of reaction " + this.getClass().getName());
    return true;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package affectedEObject, final org.emftext.language.java.classifiers.Class newValue, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.createCompositeDataType(newValue);
    }
  }
}
