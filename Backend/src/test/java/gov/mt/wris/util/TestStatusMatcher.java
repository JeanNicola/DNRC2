package gov.mt.wris.util;

public interface TestStatusMatcher {
    TestResponse is(int status);

    TestResponse is1xxInformational();

    TestResponse is2xxSuccessful();

    TestResponse is3xxRedirection();

    TestResponse is4xxClientError();

    TestResponse is5xxServerError();

    TestResponse isAccepted();

    TestResponse isAlreadyReported();

    TestResponse isBadGateway();

    TestResponse isBadRequest();

    TestResponse isBandwidthLimitExceeded();

    TestResponse isCheckpoint();

    TestResponse isConflict();

    TestResponse isContinue();

    TestResponse isCreated();

    TestResponse isExpectationFailed();

    TestResponse isFailedDependency();

    TestResponse isForbidden();

    TestResponse isFound();

    TestResponse isGatewayTimeout();

    TestResponse isGone();

    TestResponse isHttpVersionNotSupported();

    TestResponse isIAmATeapot();

    TestResponse isImUsed();

    TestResponse isInsufficientStorage();

    TestResponse isInternalServerError();

    TestResponse isLengthRequired();

    TestResponse isLocked();

    TestResponse isLoopDetected();

    TestResponse isMethodNotAllowed();

    TestResponse isMovedPermanently();

    TestResponse isMultipleChoices();

    TestResponse isMultiStatus();

    TestResponse isNetworkAuthenticationRequired();

    TestResponse isNoContent();

    TestResponse isNonAuthoritativeInformation();

    TestResponse isNotAcceptable();

    TestResponse isNotExtended();

    TestResponse isNotFound();

    TestResponse isNotImplemented();

    TestResponse isNotModified();

    TestResponse isOk();

    TestResponse isPartialContent();

    TestResponse isPayloadTooLarge();

    TestResponse isPaymentRequired();

    TestResponse isPermanentRedirect();

    TestResponse isPreconditionFailed();

    TestResponse isPreconditionRequired();

    TestResponse isProcessing();

    TestResponse isProxyAuthenticationRequired();

    TestResponse isRequestedRangeNotSatisfiable();

    TestResponse isRequestHeaderFieldsTooLarge();

    TestResponse isRequestTimeout();

    TestResponse isResetContent();

    TestResponse isSeeOther();

    TestResponse isServiceUnavailable();

    TestResponse isSwitchingProtocols();

    TestResponse isTemporaryRedirect();

    TestResponse isTooEarly();

    TestResponse isTooManyRequests();

    TestResponse isUnauthorized();

    TestResponse isUnavailableForLegalReasons();

    TestResponse isUnprocessableEntity();

    TestResponse isUnsupportedMediaType();

    TestResponse isUpgradeRequired();

    TestResponse isUriTooLong();

    TestResponse isVariantAlsoNegotiates();

    TestResponse reason(String reason);
}
