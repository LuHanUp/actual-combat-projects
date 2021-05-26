package top.luhancc.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * ListValue对应的校验器
 *
 * @author luHan
 * @create 2020/12/21 16:49
 * @since 1.0.0
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private final Set<Integer> valueSet = new HashSet<>();

    @Override
    public void initialize(ListValue listValue) {
        int[] values = listValue.values();
        for (int value : values) {
            valueSet.add(value);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return valueSet.contains(value);
    }
}
