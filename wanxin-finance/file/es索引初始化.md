1. 创建索引`wanxin_finance_project`，分片数`3`，副本数`0`
~~~text
PUT /wanxin_finance_project
{
	"settings": {
		"number_of_shards": 3,
		"number_of_replicas": 0
	}
}
~~~
2. 创建`mapping`
~~~text
POST /wanxin_finance_project/_mapping
{
  "properties": {
    "isassignment": {
      "type": "keyword"
    },
    "amount": {
      "type": "double"
    },
    "period": {
      "type": "integer"
    },
    "repaymentway": {
      "type": "keyword"
    },
    "consumerid": {
      "type": "long"
    },
    "userno": {
      "type": "keyword"
    },
    "description": {
      "analyzer": "ik_max_word",
      "type": "text"
    },
    "annualrate": {
      "type": "double"
    },
    "type": {
      "type": "keyword"
    },
    "borrowerannualrate": {
      "type": "double"
    },
    "projectstatus": {
      "type": "keyword"
    },
    "projectno": {
      "type": "keyword"
    },
    "commissionannualrate": {
      "type": "keyword"
    },
    "name": {
      "analyzer": "ik_max_word",
      "type": "text"
    },
    "id": {
      "type": "long"
    },
    "createdate": {
      "type": "date"
    },
    "modifydate": {
      "type": "date"
    },
    "status": {
      "type": "keyword"
    }
  }
}
~~~