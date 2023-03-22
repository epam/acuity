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
// Generated on: 2013.07.02 at 08:28:42 PM MSK 
//


package com.acuity.visualisations.transform.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ForeignKeyRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ForeignKeyRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="targetTable" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fkField" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nullable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ForeignKeyRule")
public class ForeignKeyRule {

    @XmlAttribute
    protected String targetTable;
    @XmlAttribute
    protected String source;
    @XmlAttribute
    protected String target;
    @XmlAttribute
    protected String fkField;
    @XmlAttribute
    protected Boolean nullable;

    /**
     * Gets the value of the targetTable property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getTargetTable() {
        return targetTable;
    }

    /**
     * Sets the value of the targetTable property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTargetTable(String value) {
        this.targetTable = value;
    }

    /**
     * Gets the value of the source property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the target property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the fkField property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getFkField() {
        return fkField;
    }

    /**
     * Sets the value of the fkField property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFkField(String value) {
        this.fkField = value;
    }

    /**
     * Gets the value of the nullable property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public Boolean isNullable() {
        return nullable;
    }

    /**
     * Sets the value of the nullable property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setNullable(Boolean value) {
        this.nullable = value;
    }

}