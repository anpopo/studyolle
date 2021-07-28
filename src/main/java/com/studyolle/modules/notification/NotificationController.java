package com.studyolle.modules.notification;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping
    public String getNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);

        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());

        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);
        return "notification/list";
    }



    @GetMapping("/old")
    public String getOldNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, true);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, false);

        putCategorizedNotifications(model, notifications, notifications.size(), numberOfChecked);

        model.addAttribute("isNew", false);
        return "notification/list";
    }

    @DeleteMapping
    public String deleteNotifications(@CurrentUser Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotifications = new ArrayList<>();

        for (var notification : notifications) {
            switch (notification.getNotificationType()) {
                case STUDY_CREATED: newStudyNotifications.add(notification); break;
                case EVENT_ENROLLMENT: eventEnrollmentNotifications.add(notification); break;
                case STUDY_UPDATED: watchingStudyNotifications.add(notification); break;
            }
        }

        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);

    }
}
