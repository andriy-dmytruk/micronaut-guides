package example.micronaut

import groovy.transform.CompileStatic
import io.micronaut.context.condition.Condition
import io.micronaut.context.condition.ConditionContext
import io.micronaut.context.env.Environment
import io.micronaut.core.util.StringUtils

/**
 * @see <a href="https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/java-dg-roles.html">Configure IAM Roles for Amazon EC2</a>
 */
@CompileStatic
class AwsResourceAccessCondition implements Condition {

    @Override
    boolean matches(ConditionContext context) {

        if (StringUtils.isNotEmpty(System.getProperty("aws.accessKeyId")) && StringUtils.isNotEmpty(System.getProperty("aws.secretAccessKey"))) { // <1>
            return true
        }

        if (StringUtils.isNotEmpty(System.getenv("AWS_ACCESS_KEY_ID")) && StringUtils.isNotEmpty(System.getenv("AWS_SECRET_ACCESS_KEY"))) { // <2>
            return true
        }

        if (StringUtils.isNotEmpty(System.getenv("AWS_CONTAINER_CREDENTIALS_RELATIVE_URI"))) { // <3>
            return true
        }

        context && context.getBean(Environment).activeNames.contains(Environment.AMAZON_EC2) // <4>
    }
}
