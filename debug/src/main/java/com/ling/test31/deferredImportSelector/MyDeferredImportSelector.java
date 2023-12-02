package com.ling.test31.deferredImportSelector;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

public class MyDeferredImportSelector implements DeferredImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		// 返回要导入的Bean定义的类名数组
		System.out.println("MyDeferredImportSelector#selectImports()...");
		return new String[]{"com.ling.test31.deferredImportSelector.MyBeanDefinition"};
	}

	@Override
	public Class<? extends Group> getImportGroup() {
		System.out.println("getImportGroup");
		return null;
		// return MyDeferredImportSelectorGroup.class;
	}

	public static class MyDeferredImportSelectorGroup implements Group {
		private final List<Entry> imports = new ArrayList<>();

		@Override
		public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
			imports.add(new Entry(metadata,"com.ling.test31.deferredImportSelector.MyBeanDefinition"));
			System.out.println("MyDeferredImportSelectorGroup.Group");
		}

		@Override
		public Iterable<Entry> selectImports() {
			System.out.println("Group中的：selectImports方法");
			return imports;
		}
	}
}