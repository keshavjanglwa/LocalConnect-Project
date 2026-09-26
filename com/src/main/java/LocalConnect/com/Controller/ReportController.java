package LocalConnect.com.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import LocalConnect.com.Entity.Report;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Repository.ReportRepository;
import LocalConnect.com.Service.UserService;

@Controller
public class ReportController {
    
    @Autowired
    private final ReportRepository reportRepository;
    @Autowired
    private final UserService userService;

    public ReportController(ReportRepository reportRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
    }

    // A single simple endpoint used to report any kind of content
    // (activity post, activity reply, business post or group post).
    @PostMapping("/reports")
    public String createReport(@AuthenticationPrincipal UserDetails principal,
                                @RequestParam String targetType,
                                @RequestParam Long targetId,
                                @RequestParam String reason,
                                @RequestParam(required = false) String redirectTo) {
        User user = userService.getByEmailOrThrow(principal.getUsername());

        Report report = new Report();
        report.setReporter(user);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        reportRepository.save(report);

        return "redirect:" + (redirectTo != null && !redirectTo.isBlank() ? redirectTo : "/home");
    }
}
