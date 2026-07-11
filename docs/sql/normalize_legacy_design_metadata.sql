BEGIN;

UPDATE custom_designs AS design
SET design_metadata = COALESCE(
    (
        SELECT jsonb_agg(
            CASE
                WHEN layer ? 'view' THEN layer
                ELSE layer || jsonb_build_object('view', 'front')
            END
            ORDER BY position
        )::text
        FROM jsonb_array_elements(design.design_metadata::jsonb)
             WITH ORDINALITY AS item(layer, position)
    ),
    '[]'
)
WHERE design.design_metadata IS NOT NULL
  AND btrim(design.design_metadata) <> ''
  AND pg_input_is_valid(design.design_metadata, 'jsonb')
  AND jsonb_typeof(design.design_metadata::jsonb) = 'array';

UPDATE custom_designs AS design
SET back_design_metadata = COALESCE(
    (
        SELECT jsonb_agg(
            CASE
                WHEN layer ? 'view' THEN layer
                ELSE layer || jsonb_build_object('view', 'back')
            END
            ORDER BY position
        )::text
        FROM jsonb_array_elements(design.back_design_metadata::jsonb)
             WITH ORDINALITY AS item(layer, position)
    ),
    '[]'
)
WHERE design.back_design_metadata IS NOT NULL
  AND btrim(design.back_design_metadata) <> ''
  AND pg_input_is_valid(design.back_design_metadata, 'jsonb')
  AND jsonb_typeof(design.back_design_metadata::jsonb) = 'array';

UPDATE custom_designs
SET design_metadata = '[]'
WHERE design_metadata IS NULL OR btrim(design_metadata) = '';

UPDATE custom_designs
SET back_design_metadata = '[]'
WHERE back_design_metadata IS NULL OR btrim(back_design_metadata) = '';

COMMIT;
