package mir.reactions.reactionsPcmToUml.pcmToUml;

import mir.routines.pcmToUml.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.compound.CreateAndInsertNonRoot;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;

@SuppressWarnings("all")
class CreatedBasicPcmComponentReaction extends AbstractReactionRealization {
  public void executeReaction(final EChange change) {
    InsertEReference<org.palladiosimulator.pcm.repository.Repository, org.palladiosimulator.pcm.repository.BasicComponent> typedChange = ((CreateAndInsertNonRoot<org.palladiosimulator.pcm.repository.Repository, org.palladiosimulator.pcm.repository.BasicComponent>)change).getInsertChange();
    org.palladiosimulator.pcm.repository.Repository affectedEObject = typedChange.getAffectedEObject();
    EReference affectedFeature = typedChange.getAffectedFeature();
    org.palladiosimulator.pcm.repository.BasicComponent newValue = typedChange.getNewValue();
    mir.routines.pcmToUml.RoutinesFacade routinesFacade = new mir.routines.pcmToUml.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsPcmToUml.pcmToUml.CreatedBasicPcmComponentReaction.ActionUserExecution userExecution = new mir.reactions.reactionsPcmToUml.pcmToUml.CreatedBasicPcmComponentReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(affectedEObject, affectedFeature, newValue, routinesFacade);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return CreateAndInsertNonRoot.class;
  }
  
  private boolean checkChangeProperties(final EChange change) {
    InsertEReference<org.palladiosimulator.pcm.repository.Repository, org.palladiosimulator.pcm.repository.BasicComponent> relevantChange = ((CreateAndInsertNonRoot<org.palladiosimulator.pcm.repository.Repository, org.palladiosimulator.pcm.repository.BasicComponent>)change).getInsertChange();
    if (!(relevantChange.getAffectedEObject() instanceof org.palladiosimulator.pcm.repository.Repository)) {
    	return false;
    }
    if (!relevantChange.getAffectedFeature().getName().equals("components__Repository")) {
    	return false;
    }
    if (!(relevantChange.getNewValue() instanceof org.palladiosimulator.pcm.repository.BasicComponent)) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof CreateAndInsertNonRoot)) {
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
    
    public void callRoutine1(final Repository affectedEObject, final EReference affectedFeature, final BasicComponent newValue, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.createUmlComponent(newValue, "basic");
    }
  }
}
