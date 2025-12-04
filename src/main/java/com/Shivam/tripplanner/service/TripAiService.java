//package com.Shivam.tripplanner.service;
//
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TripAiService {
//
//    // 2. Rename variable type to ChatModel
//    private final ChatModel chatModel;
//
//    @Autowired
//    public TripAiService(ChatModel chatModel) {
//        this.chatModel = chatModel;
//    }
//
//    public String getTripPlan(String destination, int days, String budget, String travelers) {
//        String prompt = String.format("Generate a travel itinerary for %s for %d days for a %s trip with a %s budget. " +
//                        "Return ONLY JSON format (no markdown). Schema: { \"trip_name\": \"String\", \"hotels\": [{\"name\":\"\", \"address\":\"\", \"price\":\"\", \"image_url\":\"\"}], \"itinerary\": [{\"day\": 1, \"theme\": \"\", \"plan\": [{\"place_name\": \"\", \"details\": \"\", \"ticket_pricing\": \"\", \"time_to_travel\": \"\"}]}] }",
//                destination, days, travelers, budget);
//
//        // 3. Call the model
//        return chatModel.call(prompt);
//    }
//}