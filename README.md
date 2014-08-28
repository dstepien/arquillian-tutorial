arquillian-tutorial
===================

Simple arquillian project based on ["Getting Started"](http://arquillian.org/guides/) guides.

The project includes four maven profiles that shows how Arquillian can talk with containers:

* **arquillian-embedded-weld-ee** and **arquillian-embedded-glassfish** embed containers inside the project.
* **arquillian-managed-wildfly** starts the wildfly, runs tests and shut it down.
* **arquillian-remote-wildfly** deploys test package to already started container (wildfly server in this case), runs tests and undeploy test package.
