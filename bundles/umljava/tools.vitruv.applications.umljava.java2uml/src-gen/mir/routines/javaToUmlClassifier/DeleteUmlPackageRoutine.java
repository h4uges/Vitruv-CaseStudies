package mir.routines.javaToUmlClassifier;

import java.io.IOException;
import mir.routines.javaToUmlClassifier.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class DeleteUmlPackageRoutine extends AbstractRepairRoutineRealization {
  private RoutinesFacade actionsFacade;
  
  private DeleteUmlPackageRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.containers.Package jPackage, final org.eclipse.uml2.uml.Package uPackage) {
      return uPackage;
    }
    
    public EObject getCorrepondenceSourceUPackage(final org.emftext.language.java.containers.Package jPackage) {
      return jPackage;
    }
  }
  
  public DeleteUmlPackageRoutine(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package jPackage) {
    super(reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.javaToUmlClassifier.DeleteUmlPackageRoutine.ActionUserExecution(getExecutionState(), this);
    this.actionsFacade = new mir.routines.javaToUmlClassifier.RoutinesFacade(getExecutionState(), this);
    this.jPackage = jPackage;
  }
  
  private org.emftext.language.java.containers.Package jPackage;
  
  protected void executeRoutine() throws IOException {
    getLogger().debug("Called routine DeleteUmlPackageRoutine with input:");
    getLogger().debug("   jPackage: " + this.jPackage);
    
    org.eclipse.uml2.uml.Package uPackage = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceUPackage(jPackage), // correspondence source supplier
    	org.eclipse.uml2.uml.Package.class,
    	(org.eclipse.uml2.uml.Package _element) -> true, // correspondence precondition checker
    	null);
    if (uPackage == null) {
    	return;
    }
    registerObjectUnderModification(uPackage);
    deleteObject(userExecution.getElement1(jPackage, uPackage));
    
    postprocessElements();
  }
}
