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

package eu.tecfox.formatterservice.template.models.elements;

import java.util.List;

import eu.tecfox.formatterservice.template.models.style.Style;
import lombok.Data;

/**
 * Word document footer.
 *
 * <p>
 *     Footer holds the data for the footer in a
 *     generated Word Document.
 * </p>
 *
 * @author Valentin Laucht
 * @author Florin Schikarski
 * @version 1.0
 */
@Data
public class Footer {
    private List<String> leftSection;
    private List<String> middleSection;
    private List<String> rightSection;
    private Style style;
}
