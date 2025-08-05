package com.bonfire.files.api;

import com.bonfire.files.service.FilesService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.val;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import proto.HookRequest;
import proto.HookResponse;
import proto.MutinyHookHandlerGrpc;

import java.util.Map;
import java.util.function.Function;

import static com.bonfire.files.api.Mappers.OK;
import static com.bonfire.files.api.Mappers.toResponse;

@GrpcService
@RunOnVirtualThread
@Singleton
public class FilesTUSGrpc extends MutinyHookHandlerGrpc.HookHandlerImplBase {
    private static final Logger log = Logger.getLogger(FilesTUSGrpc.class);
    private static final String UPLOAD_KEY = "upload_key";

    private static final String HOOK_TYPE_PRE_CREATE = "pre-create";
    private static final String HOOK_TYPE_POST_FINISH = "post-finish";

    @Inject
    FilesService filesService;

    @Inject
    TUSAuthenticator authenticator;

    @ConfigProperty(name = "files.max.size")
    private Long maxSize;

    @Override
    public Uni<HookResponse> invokeHook(HookRequest request) {
        val requestId = request.getEvent().getUpload().getId();
        log.info("TUS Hook Request: " + request);
        val headers = request.getEvent().getHttpRequest().getHeaderMap();

        try {
            return authenticator.authorized(headers, initiator ->
                    validKey(request, uploadKey -> {

                        if (request.getType().equals(HOOK_TYPE_PRE_CREATE)) {
                            val claimedSize = request.getEvent().getUpload().getSize();
                            validateClaimedSize(claimedSize);

                            return filesService.createUpload(initiator, uploadKey)
                                    .map(Mappers::toResponse);

                        } else if (request.getType().equals(HOOK_TYPE_POST_FINISH)) {
                            return filesService.updateUploadStatus(uploadKey, true, initiator)
                                    .replaceWith(OK);

                        } else {
                            log.warn("Unsupported TUS hook type: " + request.getType());
                            return Uni.createFrom().item(OK);
                        }
                    }));
        } catch (TUSHookException.Unauthorized e) {
            log.warn(STR."Request id: `\{requestId}` failed: \{e.getMessage()}");
            return toResponse(e);
        } catch (TUSHookException.BadRequest e) {
            log.warn(STR."Request id: `\{requestId}` failed: \{e.getMessage()}");
            return toResponse(e);
        } catch (RuntimeException e) {
            log.warn(STR."Request id: `\{requestId}` failed: \{e.getMessage()}");
            throw e;
        }
    }

    private <Out> Out validKey(HookRequest request, Function<String, Out> fn) {
        Map<String, String> metadata = request.getEvent().getUpload().getMetaDataMap();
        String uploadKey = metadata.get(UPLOAD_KEY);

        if (uploadKey == null || uploadKey.isEmpty()) {
            throw new TUSHookException.BadRequest("upload_key not found");
        }
        return fn.apply(uploadKey);
    }

    private void validateClaimedSize(Long claimedSize) {
        if (claimedSize > maxSize) {
            throw new TUSHookException.BadRequest("File size exceded limit");
        }
    }
}
