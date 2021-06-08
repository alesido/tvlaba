import de.fayard.refreshVersions.bootstrapRefreshVersions

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions()

include(":mobile", ":tv", ":DomainTv", ":DomainVod", ":DomainUser", ":PresentationTv", ":PresentationVod", ":Presentation", ":Domain", ":Data", ":DataTv", ":Remote", ":MoiDom", ":Local", ":leanback")
