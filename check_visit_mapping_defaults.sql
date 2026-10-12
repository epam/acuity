SELECT
    mfr.mfr_id,
    mfr.mfr_name,
    men.men_name AS entity_name,
    mfi.mfi_name AS field_name,
    mmr.mmr_value AS default_value,
    array_remove(array_agg(mcr.mcr_name ORDER BY mcr.mcr_name), NULL) AS source_columns
FROM map_file_rule mfr
JOIN map_mapping_rule mmr ON mmr.mmr_mfr_id = mfr.mfr_id
JOIN map_mapping_rule_field mrf ON mrf.mrf_mmr_id = mmr.mmr_id
JOIN map_field mfi ON mfi.mfi_id = mrf.mrf_mfi_id
JOIN map_entity men ON men.men_id = mfi.mfi_men_id
LEFT JOIN map_column_rule mcr ON mcr.mcr_mmr_id = mmr.mmr_id
WHERE mfr.mfr_msr_id = 1
  AND (
      mfr.mfr_name ILIKE '%visit%'
      OR men.men_name = 'Visit'
      OR mfi.mfi_name ILIKE '%study%'
  )
GROUP BY mfr.mfr_id, mfr.mfr_name, men.men_name, mfi.mfi_name, mmr.mmr_value
ORDER BY mfr.mfr_name, men.men_name, mfi.mfi_name;
