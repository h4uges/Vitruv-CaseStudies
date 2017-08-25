package mir.routines.pcm2EjbJava;

import java.io.IOException;
import mir.routines.pcm2EjbJava.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.ClassMethod;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class UpdateSEFFImplementingMethodNameRoutine extends AbstractRepairRoutineRealization {
  private RoutinesFacade actionsFacade;
  
  private UpdateSEFFImplementingMethodNameRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final ServiceEffectSpecification seff, final ClassMethod classMethod) {
      return classMethod;
    }
    
    public void update0Element(final ServiceEffectSpecification seff, final ClassMethod classMethod) {
      classMethod.setName(seff.getDescribedService__SEFF().getEntityName());
    }
    
    public EObject getCorrepondenceSourceClassMethod(final ServiceEffectSpecification seff) {
      return seff;
    }
  }
  
  public UpdateSEFFImplementingMethodNameRoutine(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ServiceEffectSpecification seff) {
    super(reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.pcm2EjbJava.UpdateSEFFImplementingMethodNameRoutine.ActionUserExecution(getExecutionState(), this);
    this.actionsFacade = new mir.routines.pcm2EjbJava.RoutinesFacade(getExecutionState(), this);
    this.seff = seff;
  }
  
  private ServiceEffectSpecification seff;
  
  protected void executeRoutine() throws IOException {
    getLogger().debug("Called routine UpdateSEFFImplementingMethodNameRoutine with input:");
    getLogger().debug("   seff: " + this.seff);
    
    org.emftext.language.java.members.ClassMethod classMethod = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceClassMethod(seff), // correspondence source supplier
    	org.emftext.language.java.members.ClassMethod.class,
    	(org.emftext.language.java.members.ClassMethod _element) -> true, // correspondence precondition checker
    	null);
    if (classMethod == null) {
    	return;
    }
    registerObjectUnderModification(classMethod);
    // val updatedElement userExecution.getElement1(seff, classMethod);
    userExecution.update0Element(seff, classMethod);
    
    postprocessElements();
  }
}
