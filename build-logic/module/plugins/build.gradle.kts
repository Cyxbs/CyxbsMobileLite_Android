plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(project(":module:api"))
  implementation(project(":module:cyxbs:applications"))
  implementation(project(":module:cyxbs:components"))
  implementation(project(":module:cyxbs:functions"))
  implementation(project(":module:cyxbs:pages"))
  implementation(project(":dependencies"))
}