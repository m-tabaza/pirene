# Pirene
This project aims to make it easy to give end-users the power of automation. In some sense, Pirene is an embedded programming language that you can expose directly to your users via Web UI, or HTTP API. You may also call it an *embedded workflow builder*.

Some use cases include:
* **Business automation** - Let members of your organization (or even clients) use drag-and-drop components to automate their work using *your code*..
* **Bot building** - Let your users build state machines using predefined actions.
* **Authorization** - Let members of your organization build their own decision trees using predefined predicates; you then invoke these authorization flows when access to a particular resource is requested.

Pirene aims to enable this by providing the following:
* A way for you (the developer) to define blocks for your users to use in their workflows
* An interpreter that runs your users' workflows
* A way to serialize your users' workflows to JSON for easy storage
* An HTTP API that you (or your users) can use to invoke workflows

This project is still at a very early stage of development. I **do not** recommend using any code in this repository in production yet.
