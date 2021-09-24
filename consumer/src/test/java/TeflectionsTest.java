import com.common.annotation.RpcReference;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.util.Set;

public class TeflectionsTest {
    public static void main(String[] args) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("com.provider.controller")
                .addScanners(new SubTypesScanner())
                .addScanners(new FieldAnnotationsScanner()));
        Set<Field> fieldsAnnotatedWith = reflections.getFieldsAnnotatedWith(RpcReference.class);
        fieldsAnnotatedWith.stream().forEach(field -> {
            Class<?> type = field.getType();
            System.out.println(type.getName());
        });
    }
}
