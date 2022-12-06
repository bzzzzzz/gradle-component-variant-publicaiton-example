package org.example;

import java.util.Collections;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.base.plugins.LifecycleBasePlugin;


public class ComponentModificationPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getPlugins().apply("java-library");

    Configuration configuration = project.getConfigurations().create("additionalVariant", c -> {
      c.setCanBeResolved(true);
      c.setCanBeConsumed(true);
      c.setVisible(true);
      c.setTransitive(true);
    });

    TaskProvider<Jar> jarTask = project.getTasks().register("customJar", Jar.class, task -> {
      task.getManifest().attributes(Collections.singletonMap("Meow", "Meow!"));
      task.getArchiveBaseName().set(project.getName());
      task.getArchiveAppendix().set("custom");
//      task.getArchiveClassifier().set("custom");
    });

    project.getTasks().named(LifecycleBasePlugin.ASSEMBLE_TASK_NAME, t -> t.dependsOn(jarTask));

    project.getArtifacts().add(configuration.getName(), jarTask);

    project.getComponents().named("java", AdhocComponentWithVariants.class,
        java -> java.addVariantsFromConfiguration(configuration, details -> {
          details.mapToOptional();
          details.mapToMavenScope("compile");
        }));
  }
}
