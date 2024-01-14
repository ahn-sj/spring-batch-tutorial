package org.tally.batchtutorial.job.validatedParam.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

@Slf4j
public class FileParamValidator implements JobParametersValidator {

    @Override
    public void validate(final JobParameters parameters) throws JobParametersInvalidException { // JobParameterValidator

        String fileName = parameters.getString("fileName");

        if(!StringUtils.endsWithIgnoreCase(fileName, "csv")) {
            throw new JobParametersInvalidException("파일 형식이 csv 가 아닙니다.");
        }
        log.info("FileParamValidator success. input = {}", fileName);
    }
}
