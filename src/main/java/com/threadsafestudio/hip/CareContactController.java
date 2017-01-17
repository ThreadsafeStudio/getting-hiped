package com.threadsafestudio.hip;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import se.hip.sdk.api.Api;
import se.hip.sdk.api.GetCareContactsBuilder;
import se.hip.sdk.api.RegisterPdlConsentBuilder;
import se.hip.sdk.api.carecontacts.CareContact;
import se.hip.sdk.api.core.*;
import se.hip.sdk.api.operation.GetCareContacts;
import se.hip.sdk.api.operation.RegisterPdlConsent;
import se.hip.sdk.api.query.result.DataResultSet;

/**
 * Created by pascal on 2017-01-10.
 */
@Controller
public class CareContactController {
    @Autowired
    private Api api;

    @RequestMapping("/carecontacts")
    @ResponseBody
    public Response<DataResultSet<CareContact>> careDocumentation() {
        final CareActorPrincipal careActor = PrincipalFactory.createCareActorPrincipal(
                "sessionId",
                "Vård och behandling",
                "assignmentId",
                "careActorId",
                "careUnitId",
                "careProviderId",
                "careActorHsaTitle",
                "careActorPrescriptionCode",
                "Läsa;alla;SJF"
        );
        final SubjectOfCare soc = DefaultSubjectOfCare.create("191212121212");

        // Register a consent for
        // Tolvan (191212121212)
        final RegisterPdlConsent postConsent = api.getOperationBuilder(RegisterPdlConsentBuilder.class)
                .as(careActor)
                .forSubjectOfCare(soc)
                .isEmergency(false)
                .isForRequestingCareActorOnly(false)
                .validFrom(DateTime.now().minusYears(1).toDate())
                .expiresAt(DateTime.now().toDate())
                .build();

        final Response resp = postConsent.execute();
        if (resp.getStatus().getErrors().isEmpty()) {
            final GetCareContacts request = api.getOperationBuilder(GetCareContactsBuilder.class)
                    .as(careActor)
                    .withSubjectOfCare(DefaultSubjectOfCare.create("191212121212"))
                    .build();
            final Response<DataResultSet<CareContact>> response = request.execute();
            return response;
        } else {
            return null;
        }
    }
}
