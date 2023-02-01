/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.tecfox.formatterservice.template.models.section.elements;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.Data;

/**
 * Class that holds the dates for a {@link DateRangeElement}
 *
 * @author Valentin Laucht
 * @author Florin Schikarski
 * @version 1.0
 */
@Data
public class DateRange {
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate from;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate to;


    @Override 
    public String toString() {

        return formatMonth(from.getMonthValue()) + "/" + from.getYear() + " - " + formatMonth(to.getMonthValue()) + "/" + to.getYear();
    }


    public static String formatMonth(Integer month) {

        return (month > 0 && month < 10) ? "0" + month : month.toString();
    }
}
