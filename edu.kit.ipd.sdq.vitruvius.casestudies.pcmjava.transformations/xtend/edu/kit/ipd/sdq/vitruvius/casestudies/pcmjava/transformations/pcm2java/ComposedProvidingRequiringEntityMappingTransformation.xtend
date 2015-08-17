package edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.transformations.pcm2java

import org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity
import org.palladiosimulator.pcm.core.entity.NamedElement
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.PCMJaMoPPNamespace
import edu.kit.ipd.sdq.vitruvius.framework.run.transformationexecuter.EmptyEObjectMappingTransformation
import edu.kit.ipd.sdq.vitruvius.framework.run.transformationexecuter.TransformationUtils
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.emftext.language.java.containers.Package

/**
 * base class for RepositoryComponentMappingTransformation and SystemMappingTransformation
 */
abstract class ComposedProvidingRequiringEntityMappingTransformation extends EmptyEObjectMappingTransformation {

	override setCorrespondenceForFeatures() {
		PCM2JaMoPPUtils.addEntityName2NameCorrespondence(featureCorrespondenceMap)
	}

	def Package getParentPackage(EObject eObject)

	/**
	 * called when a ComposedProvidingRequiriungEntity has been created
	 * --> create a package, a compilation unit and a class for the entity
	 */
	override createEObject(EObject eObject) {
		val ComposedProvidingRequiringEntity composedEntity = eObject as ComposedProvidingRequiringEntity
		val Package parentPackage = getParentPackage(eObject)

		//create all elements
		val createdEObjects = PCM2JaMoPPUtils.createPackageCompilationUnitAndJaMoPPClass(composedEntity, parentPackage)

		return createdEObjects
	}

	override removeEObject(EObject eObject) {
		return correspondenceInstance.getAllCorrespondingEObjects(eObject)
	}

	override updateSingleValuedEAttribute(EObject eObject, EAttribute affectedAttribute, Object oldValue,
		Object newValue) {
		return PCM2JaMoPPUtils.updateNameAsSingleValuedEAttribute(eObject, affectedAttribute, oldValue, newValue,
			featureCorrespondenceMap, correspondenceInstance)
	}

	/**
	 * called when a AssemblyContext has been added
	 */
	override createNonRootEObjectInList(EObject newAffectedEObject, EObject oldAffectedEObject,
		EReference affectedReference, EObject newValue, int index, EObject[] newCorrespondingEObjects) {
		val tcr = TransformationUtils.createEmptyTransformationChangeResult
		if ((affectedReference.name.equals(PCMJaMoPPNamespace.PCM.SYSTEM_ASSEMBLY_CONTEXTS__COMPOSED_STRUCTURE) ||
			affectedReference.name.equals(PCMJaMoPPNamespace.PCM.COMPONENT_PROVIDED_ROLES_INTERFACE_PROVIDING_ENTITY) ||
			affectedReference.name.equals(PCMJaMoPPNamespace.PCM.COMPONENT_REQUIRED_ROLES_INTERFACE_REQUIRING_ENTITY)) &&
			newValue instanceof NamedElement) {
			PCM2JaMoPPUtils.
				handleAssemblyContextAddedAsNonRootEObjectInList(newAffectedEObject as ComposedProvidingRequiringEntity,
					newValue as NamedElement, newCorrespondingEObjects, tcr, correspondenceInstance)
		} 
		return tcr
	}
	
	/**
	 * TODO: copied from BasicComponent: refactor 
	 * called when OperationProvidedRole has been removed from the current ComposedProvidingRequiringEntity
	 */
	override removeNonContainmentEReference(EObject affectedEObject, EReference affectedReference, EObject oldValue,
		int index) {

		//provided role removed - deletion of eobject should already be done in OperationProvidedRoleMappingTransformation - mark bc to save
		if (affectedReference.name.equals(PCMJaMoPPNamespace.PCM.COMPONENT_PROVIDED_ROLES_INTERFACE_PROVIDING_ENTITY) ||
			affectedReference.name.equals(PCMJaMoPPNamespace.PCM.COMPONENT_REQUIRED_ROLES_INTERFACE_REQUIRING_ENTITY)) {
			return TransformationUtils.createTransformationChangeResultForEObjectsToSave(affectedEObject.toArray)
		}
		return TransformationUtils.createEmptyTransformationChangeResult
	}

	/**
	 * TODO: copied from BasicComponent: refactor
	 * called when an OperationProvidedRole was has been inserted in the current ComposedProvidingRequiringEntity
	 */
	override insertNonRootEObjectInContainmentList(EObject oldAffectedEObject, EObject newAffectedEObject,
		EReference affectedReference, EObject newValue) {
		if (affectedReference.name.equals(PCMJaMoPPNamespace.PCM.COMPONENT_PROVIDED_ROLES_INTERFACE_PROVIDING_ENTITY) ||
			affectedReference.name.equals(PCMJaMoPPNamespace.PCM.COMPONENT_REQUIRED_ROLES_INTERFACE_REQUIRING_ENTITY)) {
			if (null != newAffectedEObject) {
				return TransformationUtils.createTransformationChangeResultForEObjectsToSave(newAffectedEObject.toArray)
			}
		}
		return TransformationUtils.createEmptyTransformationChangeResult
	}

}