/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.04 at 08:28:32 PM MSK 
//


package com.acuity.visualisations.transform.ndtrule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NDTFilterRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NDTFilterRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/NewDataTransformation}NDTNamedRule">
 *       &lt;attribute name="equal" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NDTFilterRule")
public class NDTFilterRule
        extends NDTNamedRule {

    @XmlAttribute
    protected String equal;

    /**
     * Gets the value of the equal property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getEqual() {
        return equal;
    }

    /**
     * Sets the value of the equal property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEqual(String value) {
        this.equal = value;
    }

}