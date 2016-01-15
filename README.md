# SearchService

**GET /restaurants**  - search restaurants

| Fields        | Description                                             | Type                                | Required | 
| ------------- |:--------------------------------------------------------|:----------------------------------- |:--------:|
| keyword       | keyword                                                 | String                              | optional |
| offset        | offset                                                  | Integer                             | optional |
| limit         | limit                                                   | Integer                             | optional |
| sort_by       | sort critera. default = relevance                       | Enum (hotness, distance, relevance) | optional |
| sort_order    | sort order. default = decrease                          | Enum (increase, decrease)           | optional |
| parameters    | tuning parameters                                       | **TuningParams**                    | optional |
| output        | output selector. <br />To control which field to return.| **Output**                          | optional |
| user_location | user location                                           | **Location**                        | optional |
| range         | distance range constraint                               | **Range**                           | optional |




**GET /dish** - search dishes

| Fields        | Description                                             | Type                                | Required | 
| ------------- |:--------------------------------------------------------|:----------------------------------- |:--------:|
| keyword       | keyword                                                 | String                              | optional |
| offset        | offset                                                  | Integer                             | optional |
| limit         | limit                                                   | Integer                             | optional |
| sort_by       | sort critera. default = relevance                       | Enum (hotness, distance, relevance) | optional |
| sort_order    | sort order. default = decrease                          | Enum (increase, decrease)           | optional |
| parameters    | tuning parameters                                       | **TuningParams**                    | optional |
| output        | output selector. <br />To control which field to return.| **Output**                          | optional |
| user_location | user location                                           | **Location**                        | optional |
| range         | distance range constraint                               | **Range**                           | optional |
| restaurant_id | the restaurant this dish belong to                      | String                              | optional |
| menu_id       | the menu       this dish belong to                      | String                              | optional |




**GET /lists** - search dish lists

| Fields        | Description                                             | Type                                | Required | 
| ------------- |:--------------------------------------------------------|:----------------------------------- |:--------:|
| keyword       | keyword                                                 | String                              | optional |
| offset        | offset                                                  | Integer                             | optional |
| limit         | limit                                                   | Integer                             | optional |
| sort_by       | sort critera. default = relevance                       | Enum (hotness, distance, relevance) | optional |
| sort_order    | sort order. default = decrease                          | Enum (increase, decrease)           | optional |
| parameters    | tuning parameters                                       | **TuningParams**                    | optional |
| output        | output selector. <br />To control which field to return.| **Output**                          | optional |
| user_location | user location                                           | **Location**                        | optional |
| range         | distance range constraint                               | **Range**                           | optional |




**Custom Objects**    
**TuningParams**   

| Fields                    | Description                                                      | Type  | Required | 
| --------------------------|:-----------------------------------------------------------------|:------|:--------:|
| relevance_score_threshold | set different relevance score threshold to see how results change| float | optional |


**Output**    

| Fields | Description                             | Type               | Required | 
| -------|:----------------------------------------|:-------------------|:--------:|
| fields | select which fields to return           | List<String>       | optional |
| params | parameters needed to control the output | **OutputParams**   | optional |


**OutputParams**    

| Fields | Description                                                   | Type         | Required | Default | 
| -------|:--------------------------------------------------------------|:-------------|:--------:|:-------:|
| distanc_unit | control what unit to use for the distance in the output | Enum (mi, km)| optional | mi      |


**Range**    

| Fields   | Description          | Type         | Required | 
| ---------|:---------------------|:-------------|:--------:|
| center   | center               | **Location** | required |
| distance | distance from center | **Distance** | required |


**Location**    

| Fields | Description | Type   | Required | 
| -------|:------------|:-------|:--------:|
| lat    | latitude    | double | required |
| lon    | longitude   | double | required |


**Distance**    

| Fields | Description    | Type             | Required | 
| -------|:---------------|:-----------------|:--------:|
| value  | distance value | double           | required |
| lon    | distance unit  | Enum (mi, km)    | optional |







