# Hodaka
穗高客制程式.

## Program One：BOMLogic

| No        | Rule          | Type  |
| :-------------: |:-------------| :-----:|
| 1 | 組成的料件類型僅能為原料/回收料 | 穗高配方維護作業規範 |
| 2 | 每筆組成的[原料/副產品]欄位不得為空      | 穗高配方維護作業規範 |
| 3 | 每筆組成的[比例型態 ]欄位不得為空      | 穗高配方維護作業規範 |
| 4 | 每筆組成的[BOM單位]欄位不得為空      | 穗高配方維護作業規範 |
| 5 | 每筆副產品組成的[BOM數量]欄位資訊不可為空 其他不可為0      | 穗高配方維護作業規範 |
| 6 | 每筆組成的[Find Num]格式必須為四碼(ex. 0010, 0020, 0100…)| 穗高配方維護作業規範 |
| 7 | 配方廠區須在每個BOM裏對應廠區啓用|穗高配方維護作業規範|
| 8 | 至少包含一筆原料，且數量不得為0	      | EBS整合需求 |
| 9 | 主單位/次單位都是kg/pc BOM單位則只能為kg/pc	      | EBS整合需求 |
| 10 | 主單位/次單位都是kg or PC 標準單位轉換率只能是1      | EBS整合需求 |

## Program Two：AutoAddBOM
透過變更單自動將配方料號放至成品料號BOM頁籤中並發行  

配方建置流程簽核完成後，自動依據配方上所註記的成品號碼，將配方加入至成品的BOM頁籤中。 


## Program Three: AutoRevision
自動更新revision number  
##### 程式邏輯:
Revision Number 為數字，從1開始往上  
若OLD REV為英文字母，程式將出錯

## Program Four: AutoNumber
自動依Excel檔需求建立流水號
##### 程式邏輯:
搜尋EXCEL對應的CLASSNAME，讀取CLASSNAME的規則，讀取規則對應的欄位，將結果寫入流水號
##### EXCEL規則
| 符號        | 規則          | 舉例  |
| :-------------: |:-------------| :-----:|
| 無 | 直接取對應欄位值。若欄位為list，抓取後臺描述值  | Cell：廠商代號=>第三頁.廠商代號=>[對應值]|
| $ | 直接讀取$后的值 | $產品名 => 產品名 |
## Program Five: AutoDescription
自動依Excel檔需求建立描述
##### 程式邏輯:
點擊Action Menu裏的按鈕觸發程式執行，將所有受影響料號的描述按照EXCEL產生  
##### EXCEL規則
| 符號        | 規則          | 舉例  |
| :-------------: |:-------------| :-----:|
| 無 | 直接取對應欄位值。若欄位為list，抓取後臺描述值  | Cell：廠商代號=>第三頁.廠商代號=>[對應值]|
| $ | 直接讀取$后的值 | $產品名 => 產品名 |
| ~[數字] |自動產生[數字]長度的流水號並確保不重複 | ~9 => 000000000|
| ~[欄位][數字]|直接取對應欄位值。若欄位為list，抓取後臺描述值。最後確認對應值長度與[數字]一樣| ~廠商代號4 =>第三頁.廠商代號=>【對應值】,if [對應值].length == 4 OK|

## Program Six: AddToList
表單進入某站則將表單裏P3.Text01~Text10值加入【合金碼】清單裏
若有空值或重複或長度不符則不會進站


