package com.icuxika.framework.object.base.common.validation;

import com.icuxika.framework.basic.dict.GenderType;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Repeatable(Gender.List.class)
@Documented
@Constraint(validatedBy = Gender.Validator.class)
public @interface Gender {

    String message() default "[性别]取值范围是[1, 2, 3]";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean nullable() default false;

    class Validator implements ConstraintValidator<Gender, Integer> {

        private Gender constraintAnnotation;

        @Override
        public void initialize(Gender constraintAnnotation) {
            this.constraintAnnotation = constraintAnnotation;
        }

        @Override
        public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
            if (integer == null) {
                return constraintAnnotation.nullable();
            }
            return GenderType.get(integer) != null;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Documented
    @interface List {
        Gender[] value();
    }
}
