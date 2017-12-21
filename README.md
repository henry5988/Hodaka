# Hodaka
First assignment back in Anselm Inc.

## Program One
BOMLogic.java

| No        | Rule          | Type  |
| :-------------: |:-------------| :-----:|
| 1 | 組成的料件類型僅能為原料/回收料 | 穗高配方維護作業規範 |
| 2 | 每筆組成的[原料/副產品]欄位不得為空      | 穗高配方維護作業規範 |
| 3 | 每筆組成的[比例型態 ]欄位不得為空      | 穗高配方維護作業規範 |
| 4 | 每筆組成的[BOM單位]欄位不得為空      | 穗高配方維護作業規範 |
| 5 | 每筆組成的[BOM數量]欄位資訊不可為0      | 穗高配方維護作業規範 |
| 6 | 每筆組成的[Find Num]格式必須為四碼(ex. 0010, 0020, 0100…)| 穗高配方維護作業規範 |
| 7 | 至少包含一筆原料，且數量不得為0	      | EBS整合需求 |
| 8 | 主單位/次單位都是kg/pc BOM單位則只能為kg/pc	      | EBS整合需求 |
| 9 | 主單位/次單位都是kg or PC 標準單位轉換率只能是1      | EBS整合需求 |

## Program Two
AutoAddBOM
透過變更單自動將配方料號放至成品料號BOM頁籤中並發行  

配方建置流程簽核完成後，自動依據配方上所註記的成品號碼，將配方加入至成品的BOM頁籤中。 


## Program Three:
AutoRevision
自動更新revision number

## Program Four:
AutoNumberCustomAction  
自動依Excel檔需求建立流水號
