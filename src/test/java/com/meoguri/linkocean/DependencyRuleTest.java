package com.meoguri.linkocean;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * archUnit 을 활용한 의존성 규칙 테스트
 * <li> 참고 : Naver D2 - <a href="https://d2.naver.com/helloworld/9222129">ArchUnit - UnitTest로 아키텍처 검사</a> </li>
 */
class DependencyRuleTest {

	JavaClasses importPackages = new ClassFileImporter().importPackages("com.meoguri.linkocean..");

	@Test
	void 계층형_아키텍처_의존성_테스트() {
		layeredArchitecture()
			.layer("Configuration").definedBy("..configuration..")
			.layer("Controller").definedBy(controllerDescribe())
			.layer("Service").definedBy(serviceDescribe())
			.layer("Persistence").definedBy("..persistence..")
			.whereLayer("Controller").mayNotBeAccessedByAnyLayer()
			.whereLayer("Service").mayOnlyBeAccessedByLayers("Configuration", "Controller")
			.whereLayer("Persistence").mayOnlyBeAccessedByLayers("Controller", "Service")
			.check(importPackages);
	}

	@Test
	void domain_은_infrastructure_에_접근할_수_없다() {
		String domainPackage = "com.meoguri.linkocean.domain";
		String infrastructurePackage = "com.meoguri.linkocean.infrastructure";

		denyAnyDependency(domainPackage, infrastructurePackage, importPackages);
	}

	@Test
	void util_은_core_에_접근할_수_없다() {
		String utilPackage = "com.meoguri.linkocean.util";

		String domainPackage = "com.meoguri.linkocean.domain";
		String controllerPackage = "com.meoguri.linkocean.controller";
		String infrastructurePackage = "com.meoguri.linkocean.infrastructure";

		// QBookmark.bookmark 참조가 CustomPath 에서 필요해서 주석 처리
		// CustomPath 는 util 이 아닌 domain 의 support 정도로 가도 좋을거 같음
		// denyAnyDependency(utilPackage, domainPackage, importPackages);
		denyAnyDependency(utilPackage, controllerPackage, importPackages);
		denyAnyDependency(utilPackage, infrastructurePackage, importPackages);
	}

	private void denyAnyDependency(String fromPackage, String toPackage, JavaClasses classes) {
		noClasses()
			.that()
			.resideInAPackage(matchAllClassesInPackage(fromPackage))
			.should()
			.dependOnClassesThat()
			.resideInAnyPackage(matchAllClassesInPackage(toPackage))
			.check(classes);
	}

	private String matchAllClassesInPackage(String packageName) {
		return packageName + "..";
	}

	private DescribedPredicate<JavaClass> controllerDescribe() {
		return new DescribedPredicate<>(
			"@Controller || @RestController || controller 패키지에 포함 || restdocs 패키지에 포함 || 클래스 이름에 Controller 포함") {
			@Override
			public boolean apply(JavaClass input) {
				return input.isAnnotatedWith(Controller.class)
					|| input.isAnnotatedWith(RestController.class)
					|| input.getPackage().getName().contains("controller")
					|| input.getPackage().getName().contains("restdocs");
			}
		};
	}

	private DescribedPredicate<JavaClass> serviceDescribe() {
		return new DescribedPredicate<>(
			"@Service || service 패키지에 포함 || 클래스 이름에 Service 포함 || 클래스 이름에 Scheduler 포함") {
			@Override
			public boolean apply(JavaClass input) {
				return input.isAnnotatedWith(Service.class)
					|| input.getPackage().getName().contains("service")
					|| input.getName().contains("Service")
					|| input.getName().contains("Scheduler");
			}
		};
	}
}
