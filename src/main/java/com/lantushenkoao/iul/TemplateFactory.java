package com.lantushenkoao.iul;

import java.util.Arrays;
import java.util.List;

public class TemplateFactory {

    public static final String TEMPLATE_SPLIT = "ИУЛы раздельно по документам";
    public static final String TEMPLATE_COMBINED = "ИУЛ совмещенный на комплект";

    private List<Template> templates;

    public TemplateFactory(){
        templates = Arrays.asList(
                new Template(TEMPLATE_SPLIT, "/template_split.docx"),
                new Template(TEMPLATE_COMBINED, "/template_combined.docx")
        );
    }

    public List<Template> list(){
        return templates;
    }

    public Template find(String displayName){
        return templates.stream().filter(t -> t.getDisplayName().equals(displayName))
                .findFirst().get();
    }

    public static class Template {
        private String displayName;
        private String template;

        public Template(String displayName, String template) {
            this.displayName = displayName;
            this.template = template;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getTemplate() {
            return template;
        }
    }
}
