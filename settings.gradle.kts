plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.10.1"
////                            # available:"0.11.0"
}

include(":mobile", ":tv", ":DomainTv", ":DomainVod", ":DomainUser", ":PresentationTv", ":PresentationVod", ":Presentation", ":Domain", ":Data", ":DataTv", ":DataVod", ":Remote", ":MoiDom", ":Local", ":leanback")
