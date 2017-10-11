package com.twilio.survey.util;

import com.twilio.survey.models.Question;
import com.twilio.twiml.Hangup;
import com.twilio.twiml.Pause;
import com.twilio.twiml.Say;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;

/**
 * Class responsible of returning the appropriate TwiMLResponse based on the question
 * it receives
 */
public class VoiceQuestionBuilder implements QuestionBuilder {
    private Question question;
    private String recordingInstructions =
            "Record your answer after the beep.";
    String numericInstructions =
            "For the next question select a number with the dial pad and then press the pound key";
    String booleanInstructions =
            "For the next question, press 1 for yes, and 0 for no. Then press the pound key.";
    String booleanInstructions1 =
            "For the next question, press 1 for true, and 0 for false. Then press the pound key.";
    private String errorMessage =
            "We are sorry, there are no more questions available for this survey. Good bye.";


    public VoiceQuestionBuilder(Question question) {
        this.question = question;
    }

    /**
     * Bases on the question's type, a specific method is called. This method will construct
     * the specific TwiMLResponse
     */
    @Override
    public String build() throws TwiMLException {
        switch (question.getType()) {
            case "text":
                return getRecordTwiML();
            case "numeric":
                return getGatherResponse(numericInstructions);
            case "yes-no":
                return getGatherResponse(booleanInstructions);
            case "true-false":
                return getGatherResponse(booleanInstructions1);
            default:
                return buildNoMoreQuestions();
        }
    }

    /**
     * method that returns a generic TwiMLResponse when an non existent question is requested
     */
    @Override
    public String buildNoMoreQuestions() throws TwiMLException {
        return new VoiceResponse.Builder()
                .say(new Say.Builder(errorMessage).build())
                .hangup(new Hangup())
                .build()
                .toXml();
    }

    private String getRecordTwiML() throws TwiMLException {
        return new VoiceResponse.Builder()
                .say(new Say.Builder(recordingInstructions).build())
                .pause(new Pause.Builder().build())
                .say(new Say.Builder(question.getBody()).build())
                .record(TwiMLUtil.record(question))
                .build()
                .toXml();
    }

    private String getGatherResponse(String defaultMessage) throws TwiMLException {
        return new VoiceResponse.Builder()
                .say(new Say.Builder(defaultMessage).build())
                .pause(new Pause.Builder().build())
                .say(new Say.Builder(question.getBody()).build())
                .gather(TwiMLUtil.gather(question))
                .build()
                .toXml();
    
    }
}
