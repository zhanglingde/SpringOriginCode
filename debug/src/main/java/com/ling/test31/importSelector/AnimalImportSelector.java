package com.ling.test31.importSelector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class AnimalImportSelector implements ImportSelector {

    /**
     * 返回需要注入容器的 Bean 数组
     */
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
		// 类的全路径类名数组
        return new String[]{
                Cat.class.getName(), "com.ling.test31.importSelector.Dog"
        };
    }
}
