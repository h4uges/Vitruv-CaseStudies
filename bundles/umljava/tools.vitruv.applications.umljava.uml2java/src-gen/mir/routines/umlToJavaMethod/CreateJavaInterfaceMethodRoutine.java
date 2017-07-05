package mir.routines.umlToJavaMethod;

import java.io.IOException;
import mir.routines.umlToJavaMethod.RoutinesFacade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Type;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.members.InterfaceMethod;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.impl.MembersFactoryImpl;
import org.emftext.language.java.types.TypeReference;
import tools.vitruv.applications.umljava.uml2java.UmlToJavaHelper;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateJavaInterfaceMethodRoutine extends AbstractRepairRoutineRealization {
  private RoutinesFacade actionsFacade;
  
  private CreateJavaInterfaceMethodRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void updateJavaMethodElement(final Interface uInterface, final Operation uOperation, final org.emftext.language.java.classifiers.Interface jInterface, final org.emftext.language.java.classifiers.Class customTypeClass, final InterfaceMethod javaMethod) {
      String _name = uOperation.getName();
      javaMethod.setName(_name);
      Type _type = uOperation.getType();
      CompilationUnit _containingCompilationUnit = jInterface.getContainingCompilationUnit();
      TypeReference _createTypeReferenceAndUpdateImport = UmlToJavaHelper.createTypeReferenceAndUpdateImport(_type, customTypeClass, _containingCompilationUnit, this.userInteracting);
      javaMethod.setTypeReference(_createTypeReferenceAndUpdateImport);
      javaMethod.makePublic();
    }
    
    public EObject getElement1(final Interface uInterface, final Operation uOperation, final org.emftext.language.java.classifiers.Interface jInterface, final org.emftext.language.java.classifiers.Class customTypeClass, final InterfaceMethod javaMethod) {
      return uOperation;
    }
    
    public void update0Element(final Interface uInterface, final Operation uOperation, final org.emftext.language.java.classifiers.Interface jInterface, final org.emftext.language.java.classifiers.Class customTypeClass, final InterfaceMethod javaMethod) {
      EList<Member> _members = jInterface.getMembers();
      _members.add(javaMethod);
    }
    
    public EObject getCorrepondenceSourceCustomTypeClass(final Interface uInterface, final Operation uOperation, final org.emftext.language.java.classifiers.Interface jInterface) {
      Type _type = uOperation.getType();
      return _type;
    }
    
    public EObject getElement2(final Interface uInterface, final Operation uOperation, final org.emftext.language.java.classifiers.Interface jInterface, final org.emftext.language.java.classifiers.Class customTypeClass, final InterfaceMethod javaMethod) {
      return javaMethod;
    }
    
    public EObject getElement3(final Interface uInterface, final Operation uOperation, final org.emftext.language.java.classifiers.Interface jInterface, final org.emftext.language.java.classifiers.Class customTypeClass, final InterfaceMethod javaMethod) {
      return jInterface;
    }
    
    public EObject getCorrepondenceSourceJInterface(final Interface uInterface, final Operation uOperation) {
      return uInterface;
    }
  }
  
  public CreateJavaInterfaceMethodRoutine(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface uInterface, final Operation uOperation) {
    super(reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.umlToJavaMethod.CreateJavaInterfaceMethodRoutine.ActionUserExecution(getExecutionState(), this);
    this.actionsFacade = new mir.routines.umlToJavaMethod.RoutinesFacade(getExecutionState(), this);
    this.uInterface = uInterface;this.uOperation = uOperation;
  }
  
  private Interface uInterface;
  
  private Operation uOperation;
  
  protected void executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateJavaInterfaceMethodRoutine with input:");
    getLogger().debug("   Interface: " + this.uInterface);
    getLogger().debug("   Operation: " + this.uOperation);
    
    org.emftext.language.java.classifiers.Interface jInterface = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceJInterface(uInterface, uOperation), // correspondence source supplier
    	org.emftext.language.java.classifiers.Interface.class,
    	(org.emftext.language.java.classifiers.Interface _element) -> true, // correspondence precondition checker
    	null);
    if (jInterface == null) {
    	return;
    }
    registerObjectUnderModification(jInterface);
    org.emftext.language.java.classifiers.Class customTypeClass = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceCustomTypeClass(uInterface, uOperation, jInterface), // correspondence source supplier
    	org.emftext.language.java.classifiers.Class.class,
    	(org.emftext.language.java.classifiers.Class _element) -> true, // correspondence precondition checker
    	null);
    registerObjectUnderModification(customTypeClass);
    InterfaceMethod javaMethod = MembersFactoryImpl.eINSTANCE.createInterfaceMethod();
    userExecution.updateJavaMethodElement(uInterface, uOperation, jInterface, customTypeClass, javaMethod);
    
    addCorrespondenceBetween(userExecution.getElement1(uInterface, uOperation, jInterface, customTypeClass, javaMethod), userExecution.getElement2(uInterface, uOperation, jInterface, customTypeClass, javaMethod), "");
    
    // val updatedElement userExecution.getElement3(uInterface, uOperation, jInterface, customTypeClass, javaMethod);
    userExecution.update0Element(uInterface, uOperation, jInterface, customTypeClass, javaMethod);
    
    postprocessElements();
  }
}