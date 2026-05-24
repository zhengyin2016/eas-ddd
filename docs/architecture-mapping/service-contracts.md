# 服务契约 (Service Contracts)

**编号**: EAS-ARCH-005
**版本**: 1.0
**日期**: 2026-05-27
**作者**: 张毅

## 概述

本文档定义EAS系统各核心限界上下文的北向服务契约(REST API)，采用OpenAPI 3.0规范。

## HR上下文北向服务

### 基础路径

```
/api/hr/v1
```

### API列表

#### 1. 查询员工列表

```
GET /api/hr/v1/employees
```

**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页大小，默认20 |
| status | string | 否 | 员工状态：ACTIVE/ON_LEAVE/RESIGNED |
| deptId | string | 否 | 部门ID |

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 150,
    "page": 1,
    "size": 20,
    "items": [
      {
        "id": "E001",
        "name": "张三",
        "status": "ACTIVE",
        "deptId": "D001",
        "deptName": "研发部",
        "position": "高级工程师",
        "level": "L5",
        "joinDate": "2020-03-15"
      }
    ]
  }
}
```

#### 2. 查询员工详情

```
GET /api/hr/v1/employees/{id}
```

**路径参数**:

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | string | 员工ID |

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "E001",
    "name": "张三",
    "email": "zhangsan@eas.com",
    "phone": "13800138000",
    "status": "ACTIVE",
    "dept": {
      "id": "D001",
      "name": "研发部"
    },
    "position": "高级工程师",
    "level": "L5",
    "joinDate": "2020-03-15",
    "skills": [
      {"name": "Java", "level": "专家"},
      {"name": "DDD", "level": "熟练"}
    ]
  }
}
```

#### 3. 查询可用员工

```
GET /api/hr/v1/employees/available
```

**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| startDate | string | 是 | 开始日期，ISO 8601 |
| endDate | string | 是 | 结束日期，ISO 8601 |
| skill | string | 否 | 技能筛选，可重复 |
| fteRatio | decimal | 否 | 全职比例，0.5表示兼职 |

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "employee": {
        "id": "E001",
        "name": "张三",
        "level": "L5",
        "dept": "研发部"
      },
      "availableCapacity": 0.6,
      "currentProjects": 2,
      "skills": ["Java", "DDD"]
    }
  ]
}
```

#### 4. 创建员工

```
POST /api/hr/v1/employees
```

**请求体**:

```json
{
  "name": "李四",
  "email": "lisi@eas.com",
  "phone": "13900139000",
  "deptId": "D001",
  "position": "工程师",
  "level": "L3",
  "joinDate": "2026-05-27"
}
```

**响应**:

```json
{
  "code": 201,
  "message": "created",
  "data": {
    "id": "E002",
    "name": "李四",
    "status": "ACTIVE"
  }
}
```

---

## PM上下文北向服务

### 基础路径

```
/api/pm/v1
```

### API列表

#### 1. 创建项目

```
POST /api/pm/v1/projects
```

**请求体**:

```json
{
  "name": "EAS企业协作系统",
  "description": "基于DDD的企业级协作系统",
  "customerId": "C001",
  "contractId": "CT001",
  "managerId": "E001",
  "startDate": "2026-05-01",
  "endDate": "2026-07-31",
  "budget": 500000
}
```

**响应**:

```json
{
  "code": 201,
  "message": "created",
  "data": {
    "id": "P001",
    "name": "EAS企业协作系统",
    "status": "PENDING_APPROVAL",
    "createdAt": "2026-05-27T10:30:00Z"
  }
}
```

#### 2. 查询项目详情

```
GET /api/pm/v1/projects/{id}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "P001",
    "name": "EAS企业协作系统",
    "description": "基于DDD的企业级协作系统",
    "status": "IN_PROGRESS",
    "customer": {
      "id": "C001",
      "name": "某科技公司"
    },
    "contract": {
      "id": "CT001",
      "amount": 500000
    },
    "manager": {
      "id": "E001",
      "name": "张三"
    },
    "startDate": "2026-05-01",
    "endDate": "2026-07-31",
    "progress": 35,
    "members": [
      {
        "employeeId": "E001",
        "name": "张三",
        "role": "项目经理",
        "fteRatio": 0.5
      }
    ]
  }
}
```

#### 3. 分配项目成员

```
POST /api/pm/v1/projects/{projectId}/members
```

**请求体**:

```json
{
  "employeeId": "E002",
  "role": "开发工程师",
  "fteRatio": 1.0,
  "startDate": "2026-05-01",
  "endDate": "2026-07-31"
}
```

**响应**:

```json
{
  "code": 201,
  "message": "member added",
  "data": {
    "id": "PM001",
    "employeeId": "E002",
    "employeeName": "李四",
    "role": "开发工程师",
    "fteRatio": 1.0
  }
}
```

#### 4. 创建任务

```
POST /api/pm/v1/projects/{projectId}/tasks
```

**请求体**:

```json
{
  "title": "实现HR上下文",
  "description": "完成人力资源限界上下文的开发",
  "assigneeId": "E002",
  "storyPoints": 8,
  "dueDate": "2026-06-15"
}
```

**响应**:

```json
{
  "code": 201,
  "message": "created",
  "data": {
    "id": "T001",
    "title": "实现HR上下文",
    "status": "TODO",
    "assignee": {
      "id": "E002",
      "name": "李四"
    }
  }
}
```

---

## CRM上下文北向服务

### 基础路径

```
/api/crm/v1
```

### API列表

#### 1. 创建客户

```
POST /api/crm/v1/customers
```

**请求体**:

```json
{
  "name": "某科技公司",
  "industry": "软件",
  "level": "A",
  "address": "北京市朝阳区",
  "contacts": [
    {
      "name": "王总",
      "title": "CTO",
      "phone": "13800000001",
      "email": "wang@company.com"
    }
  ]
}
```

**响应**:

```json
{
  "code": 201,
  "message": "created",
  "data": {
    "id": "C001",
    "name": "某科技公司",
    "level": "A",
    "createdAt": "2026-05-27T10:30:00Z"
  }
}
```

#### 2. 创建商机

```
POST /api/crm/v1/opportunities
```

**请求体**:

```json
{
  "customerId": "C001",
  "title": "企业协作系统项目",
  "estimatedAmount": 500000,
  "stage": "PROPOSAL",
  "expectedCloseDate": "2026-06-30"
}
```

**响应**:

```json
{
  "code": 201,
  "message": "created",
  "data": {
    "id": "O001",
    "customerId": "C001",
    "title": "企业协作系统项目",
    "stage": "PROPOSAL",
    "estimatedAmount": 500000
  }
}
```

#### 3. 创建合同

```
POST /api/crm/v1/contracts
```

**请求体**:

```json
{
  "customerId": "C001",
  "opportunityId": "O001",
  "title": "EAS系统开发合同",
  "amount": 500000,
  "startDate": "2026-05-01",
  "endDate": "2026-07-31",
  "paymentTerms": "分期付款"
}
```

**响应**:

```json
{
  "code": 201,
  "message": "created",
  "data": {
    "id": "CT001",
    "title": "EAS系统开发合同",
    "status": "ACTIVE",
    "amount": 500000
  }
}
```

---

## 组织上下文北向服务

### 基础路径

```
/api/org/v1
```

### API列表

#### 1. 查询组织树

```
GET /api/org/v1/organizations/tree
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "O001",
      "name": "总公司",
      "type": "COMPANY",
      "children": [
        {
          "id": "D001",
          "name": "研发部",
          "type": "DEPARTMENT",
          "parentId": "O001"
        }
      ]
    }
  ]
}
```

#### 2. 查询部门列表

```
GET /api/org/v1/departments
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "D001",
      "name": "研发部",
      "parentId": "O001",
      "parentName": "总公司"
    }
  ]
}
```

---

## 认证上下文北向服务

### 基础路径

```
/api/auth/v1
```

### API列表

#### 1. 用户登录

```
POST /api/auth/v1/login
```

**请求体**:

```json
{
  "username": "zhangsan",
  "password": "encrypted_password"
}
```

**响应**:

```json
{
  "code": 200,
  "message": "login success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresAt": "2026-05-28T10:30:00Z",
    "user": {
      "id": "U001",
      "username": "zhangsan",
      "employeeId": "E001",
      "roles": ["HR_MANAGER", "EMPLOYEE"]
    }
  }
}
```

---

## 通知上下文北向服务

### 基础路径

```
/api/notification/v1
```

### API列表

#### 1. 发送通知

```
POST /api/notification/v1/notifications
```

**请求体**:

```json
{
  "templateCode": "EMPLOYEE_HIRED",
  "recipients": ["E001", "E002"],
  "variables": {
    "employeeName": "李四",
    "dept": "研发部"
  },
  "channels": ["EMAIL", "ENTERPRISE_WECHAT"]
}
```

**响应**:

```json
{
  "code": 202,
  "message": "accepted",
  "data": {
    "batchId": "N001",
    "status": "PENDING"
  }
}
```

---

## 统一响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": "2026-05-27T10:30:00Z"
}
```

### 错误响应

```json
{
  "code": 400,
  "message": "Bad Request",
  "error": "VALIDATION_FAILED",
  "details": [
    {"field": "email", "message": "Invalid email format"}
  ],
  "timestamp": "2026-05-27T10:30:00Z"
}
```

### HTTP状态码

| 状态码 | 说明 |
|-------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 202 | 已接受（异步处理） |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

**变更记录**
- 2026-05-27: 初始版本，张毅创建
