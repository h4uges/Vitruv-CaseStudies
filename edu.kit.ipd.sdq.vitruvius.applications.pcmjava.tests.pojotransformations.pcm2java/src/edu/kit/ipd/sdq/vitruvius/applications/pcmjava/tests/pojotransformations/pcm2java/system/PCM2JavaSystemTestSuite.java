package edu.kit.ipd.sdq.vitruvius.applications.pcmjava.tests.pojotransformations.pcm2java.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({ AssemblyContextMappingTransformationTest.class,
	RequiredDelegationConnectorMappingTransformationTest.class, SystemMappingTransformationTest.class })
public class PCM2JavaSystemTestSuite {

}