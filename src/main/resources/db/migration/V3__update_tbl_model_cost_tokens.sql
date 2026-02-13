ALTER TABLE `tbl_model`
    CHANGE COLUMN `cost_token` `cost_token_in` DECIMAL(10,6) NOT NULL DEFAULT 0.0;

ALTER TABLE `tbl_model`
    ADD COLUMN `cost_token_out` DECIMAL(10,6) NOT NULL DEFAULT 0.0 AFTER `cost_token_in`;
