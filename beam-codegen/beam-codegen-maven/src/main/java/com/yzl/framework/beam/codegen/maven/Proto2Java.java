package com.yzl.framework.beam.codegen.maven;

import com.yzl.framework.beam.codegen.core.CodegenConfiguration;
import com.yzl.framework.beam.codegen.core.DefaultCodegen;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Mojo(name = "proto2java", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class Proto2Java extends AbstractMojo {

    private static final String DEFAULT_INPUT_DIR = "/src/main/proto/".replace('/', File.separatorChar);

//    @Parameter(
//            property = "includePom",
//            defaultValue = "false"
//    )
//    private Boolean includePom;
//    /**
//     * If "true", extract the included google.protobuf standard types and add them to protoc import path.
//     */
//    @Parameter(
//            property = "needPackage",
//            defaultValue = "false"
//    )
//    private Boolean needPackage;
//
//    @Parameter(property = "configFile")
//    private String configFile;

    @Parameter(property = "protocVersion")
    private String protocVersion;

    /**
     * Input directories that have *.proto files (or the configured extension).
     * If none specified then <b>src/main/protobuf</b> is used.
     *
     * @parameter property="inputDirectories"
     */
    @Parameter(
            property = "inputDirectories"
    )
    private File[] inputDirectories;

    /**
     * This parameter lets you specify additional include paths to protoc.
     */
    @Parameter(property = "includeDirectories")
    private File[] includeDirectories;

    /**
     * If "true", extract the included google.protobuf standard types and add them to protoc import path.
     */
    @Parameter(
            property = "includeStdTypes",
            defaultValue = "false"
    )
    private Boolean includeStdTypes;

    /**
     * Specifies output type.
     * Options: "java",  "cpp", "python", "descriptor" (default: "java"); for proto3 also: "javanano", "csharp", "objc", "ruby", "js"
     * <p>
     * Ignored when {@code <outputTargets>} is given
     *
     * @parameter property="type" default-value="java"
     */
    @Parameter(
            property = "type",
            defaultValue = "java"
    )
    private String type;
    /**
     * Path to the protoc plugin that generates code for the specified {@link #type}.
     * <p>
     * Ignored when {@code <outputTargets>} is given
     *
     * @parameter property="pluginPath"
     */
    @Parameter(property = "pluginPath")
    private String pluginPath;

    /**
     * Maven artifact coordinates of the protoc plugin that generates code for the specified {@link #type}.
     * Format: "groupId:artifactId:version" (eg, "io.grpc:protoc-gen-grpc-java:1.0.1")
     * <p>
     * Ignored when {@code <outputTargets>} is given
     *
     * @parameter property="pluginArtifact"
     */
    @Parameter(property = "pluginArtifact")
    private String pluginArtifact;

    /**
     * Output directory for the generated java files. Defaults to
     * "${project.build.directory}/generated-sources" or
     * "${project.build.directory}/generated-test-sources"
     * depending on the addSources parameter
     * <p>
     * Ignored when {@code <outputTargets>} is given
     *
     * @parameter property="outputDirectory"
     */
    @Parameter(
            property = "outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources"
    )
    private File outputDirectory;

    /**
     * Default extension for protobuf files
     */
    @Parameter(
            property = "extension",
            defaultValue = ".proto"
    )
    private String extension;

    /**
     * This parameter allows to use a protoc binary instead of the protoc-jar bundle
     * used to store where proto.exe stores
     *
     * @parameter property="protocCommand"
     */
    @Parameter(property = "protocCommand")
    private String protocCommand;

    /**
     * Maven artifact coordinates of the protoc binary to use
     * Format: "groupId:artifactId:version" (eg, "com.google.protobuf:protoc:3.1.0")
     *
     * @parameter property="protocArtifact"
     */
    @Parameter(property = "protocArtifact")
    private String protocArtifact;


    @Parameter(
            property = "project",
            required = true,
            readonly = true
    )
    private MavenProject project;


    @Parameter(
            defaultValue = "${localRepository}",
            required = true,
            readonly = true
    )
    private ArtifactRepository localRepository;


    @Parameter(
            defaultValue = "${project.remoteArtifactRepositories}",
            required = true,
            readonly = true
    )
    private List<ArtifactRepository> remoteRepositories;


    @Parameter(
            required = true,
            defaultValue = "${project.build.directory}/protoc-dependencies"
    )
    private File protocDependenciesPath;

    @Component
    private ArtifactFactory artifactFactory;

    @Component
    private ArtifactResolver artifactResolver;

//    @Component
//    private BuildContext buildContext;

    @Override
    public void execute() throws MojoExecutionException {
        log("Generating a POJOs&BeamInterface for each *.proto file.");
        //config file first
//        CodegenConfiguration codegenConfiguration=CodegenConfiguration.fromFile(configFile);
//        if(codegenConfiguration == null)
//            codegenConfiguration = new CodegenConfiguration();
        CodegenConfiguration codegenConfiguration = new CodegenConfiguration();
        //todo needs further consideration
        if (isNotEmpty(protocVersion))
            codegenConfiguration.setProtocVersion(protocVersion);
        if (outputDirectory != null)
            codegenConfiguration.setOutputDirectory(outputDirectory);
        if (inputDirectories != null) {
            if (inputDirectories.length == 0) {
                File inputDir = new File(project.getBasedir().getAbsolutePath() + DEFAULT_INPUT_DIR);
                inputDirectories = new File[]{inputDir};
            }
            codegenConfiguration.setInputDirectories(inputDirectories);
        }
        if (includeDirectories != null)
            codegenConfiguration.setIncludeDirectories(includeDirectories);
        if (includeStdTypes != null)
            codegenConfiguration.setIncludeStdTypes(includeStdTypes);
        if (isNotEmpty(type))
            codegenConfiguration.setType(type);
        if (isNotEmpty(extension))
            codegenConfiguration.setExtension(extension);
        if (isNotEmpty(pluginPath))
            codegenConfiguration.setPluginPath(pluginPath);
        if (isNotEmpty(pluginArtifact))
            codegenConfiguration.setPluginArtifact(pluginArtifact);
        if (isNotEmpty(protocCommand))
            codegenConfiguration.setProtocCommand(protocCommand);
        if (isNotEmpty(protocArtifact))
            codegenConfiguration.setProtocArtifact(protocArtifact);
        if (project != null)
            codegenConfiguration.setProject(project);
        if (localRepository != null)
            codegenConfiguration.setLocalRepository(localRepository);
        if (remoteRepositories != null)
            codegenConfiguration.setRemoteRepositories(remoteRepositories);
        if (artifactFactory != null)
            codegenConfiguration.setArtifactFactory(artifactFactory);
        if (artifactResolver != null)
            codegenConfiguration.setArtifactResolver(artifactResolver);
        if (protocDependenciesPath != null)
            codegenConfiguration.setProtocDependenciesPath(protocDependenciesPath);
//        if (buildContext != null)
//            codegenConfiguration.setBuildContext(buildContext);

        try {
            new DefaultCodegen()
                    .setCodegenConfiguration(codegenConfiguration)
                    .generate();

            log("Succeed in generating a POJOs&BeamInterface for each *.proto file.");

        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException(
                    "Code generation failed. See above for the full exception.");
        }
    }


    private void log(String log) {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("    " + log + "  ");
        getLog().info("------------------------------------------------------------------------");
    }

}
