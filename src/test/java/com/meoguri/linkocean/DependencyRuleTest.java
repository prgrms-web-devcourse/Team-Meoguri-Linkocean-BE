package com.meoguri.linkocean;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

import org.junit.jupiter.api.Disabled;
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

	/**
	 * 레이어 아키텍처의 한계 중 하나가 도메인이 인프라(영속성) 레이어에 의존하게 되는 것입니다.
	 * 이런 문제는 DIP를 활용하면 해결할 수 있는 부분도 있습니다. LinkMetadata 도메인에서가 딱 그런 경우 같습니다.
	 * 그래서 해당 제약 조건은 없어져도 될 것 같습니다!
	 */
	@Disabled
	@Test
	void infrastructure_는_domain_에_접근할_수_없다() {
		String infrastructurePackage = "com.meoguri.linkocean.infrastructure";
		String domainPackage = "com.meoguri.linkocean.domain";

		denyAnyDependency(infrastructurePackage, domainPackage, importPackages);
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
