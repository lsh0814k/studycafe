package com.studycafe.modules;

import com.studycafe.StudycafeApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = StudycafeApplication.class)
public class PackageDependencyTests {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";
    private static final String LANG = "java.lang..";
    private static final String MODULES = "com.studycafe.modules..";

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage(MODULES)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(MODULES);

    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(STUDY)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(STUDY, EVENT, ZONE, TAG);

    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(STUDY, ACCOUNT, EVENT);

    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(ACCOUNT, TAG, ZONE, LANG);

    @ArchTest
    ArchRule cycleCheck = slices().matching("com.studycafe.modules.(*)..")
            .should().beFreeOfCycles();
}


