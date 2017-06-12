package cn.panda.client.processor;

import cn.panda.client.executor.JobCompletedHandler;
import cn.panda.remoting.Channel;
import cn.panda.remoting.RemotingProcessor;
import cn.panda.remoting.exception.RemotingCommandException;
import cn.panda.remoting.protocol.JobProtos;
import cn.panda.remoting.protocol.RemotingCommand;
import cn.panda.remoting.protocol.command.JobFinishedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobFinishedProcessor implements RemotingProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobFinishedProcessor.class);

    JobCompletedHandler jobCompletedHandler;

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws
            RemotingCommandException {
        try {
            JobFinishedRequest requestBody = request.getBody();
            jobCompletedHandler.onComplete(requestBody.getJobResults());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return RemotingCommand.createResponseCommand(JobProtos.ResponseCode.JOB_NOTIFY_SUCCESS
                        .code(),
                "received successful");

    }
}
