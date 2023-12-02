// package com.ling.test31.bean;
//
// import org.springframework.context.annotation.ConfigurationClassParser;
// import org.springframework.context.annotation.DeferredImportSelector;
//
// import java.util.function.Predicate;
//
// public class MyDeferredImportSelectorGrouping implements ConfigurationClassParser.DeferredImportSelectorGrouping {
//     @Override
//     public Class<? extends DeferredImportSelector>[] getImports() {
//         // 返回要分组导入的选择器类数组
//         return new Class[]{MyDeferredImportSelector.class};
//     }
//
//     @Override
//     public Predicate<String> getCandidateFilter() {
//         // 返回候选过滤器，用于选择性导入Bean定义
//         return packageName -> packageName.startsWith("com.ling.test31.bean");
//     }
// }