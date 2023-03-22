package org.digitalecmt.etarget.dao;

/*-
 * #%L
 * eTarget Maven Webapp
 * %%
 * Copyright (C) 2017 - 2021 digital ECMT
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.List;

import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.FMSample;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.ShortVariant;

public interface GenomicsDataDAO {
	List<ShortVariant> getShortVariants(Integer measurement_gene_panel_id);
	List<ShortVariant> getShortVariantsForPerson(String source_id, Integer person_id);
	List<CopyNumberAlteration> getCopyNumberAlterations(Integer measurement_gene_panel_id);
	List<CopyNumberAlteration> getCopyNumberAlterationsForPerson(String source_id, Integer person_id);
	List <Rearrangement> getRearrangements(Integer measurement_gene_panel_id);
	List <Rearrangement> getRearrangementsForPerson(String source_id, Integer person_id);
	List <FMSample> getBloodSamplesForPerson(String source_id, Integer person_id);
	List <FMSample> getTumourSamplesForPerson(String source_id, Integer person_id);
	ShortVariant getShortVariantByGeneVariantID(Integer gene_variant_id);
	CopyNumberAlteration getCopyNumberAlterationByGeneVariantID(Integer gene_variant_id);
	Rearrangement getRearrangementByGeneVariantID(Integer gene_variant_id);
	ShortVariant getTumourShortVariantByGeneVariantID(Integer gene_variant_id);
	CopyNumberAlteration getTumourCopyNumberAlterationByGeneVariantID(Integer gene_variant_id);
	Rearrangement getTumourRearrangementByGeneVariantID(Integer gene_variant_id);
//	Integer getTumourBaseline(Integer measurement_gene_panel_id);
}
