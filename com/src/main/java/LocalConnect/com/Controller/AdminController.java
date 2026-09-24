package LocalConnect.com.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import LocalConnect.com.Entity.Report;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Repository.ReportRepository;
import LocalConnect.com.Repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;

    public AdminController(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();
        List<User> users = userRepository.findAll();

        model.addAttribute("reports", reports);
        model.addAttribute("users", users);
        return "admin";
    }

    @PostMapping("/reports/{id}/status")
    public String updateReportStatus(@PathVariable Long id, @RequestParam String status) {
        reportRepository.findById(id).ifPresent(report -> {
            report.setStatus(status);
            reportRepository.save(report);
        });
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setIsEnable(!user.getIsEnable());
        userRepository.save(user);
        return "redirect:/admin";
    }
}