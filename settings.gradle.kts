plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.10.1"
////                            # available:"0.10.1"
}

include(":mobile", ":tv", ":DomainTv", ":DomainVod", ":DomainUser", ":PresentationTv", ":PresentationVod", ":Presentation", ":Domain", ":Data", ":DataTv", ":Remote", ":MoiDom", ":Local", ":leanback")
