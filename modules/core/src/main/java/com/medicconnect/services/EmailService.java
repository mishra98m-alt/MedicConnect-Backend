package com.medicconnect.services;

import com.medicconnect.dto.PersonDTO;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailUser;

    @Value("${spring.mail.password}")
    private String emailPass;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // ---------------------------------------------------------
    //  EMAIL SESSION
    // ---------------------------------------------------------
    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUser, emailPass);
            }
        });
    }

    public void sendEmail(String to, String subject, String htmlBody)
            throws MessagingException {

        MimeMessage message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(emailUser));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");
        message.setContent(htmlBody, "text/html; charset=utf-8");
        Transport.send(message);
    }

    // ---------------------------------------------------------
    //  TEMPLATE WRAPPER  (Fix: escaped % → %%)
    // ---------------------------------------------------------
    private String wrapTemplate(String title, String bodyContent) {

        return """
        <html>
        <body style="background-color:#f4f8fb; margin:0; padding:0; font-family:'Segoe UI',Arial,sans-serif;">

            <table width="100%%" cellspacing="0" cellpadding="0" style="margin:0; padding:30px 0;">
                <tr>
                    <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background:white; border-radius:12px; padding:20px; box-shadow:0 4px 12px rgba(0,0,0,0.08);">

                            <tr>
                                <td align="center" style="padding-bottom:20px;">
                                    <h1 style="color:#2563eb; font-size:24px; margin:0;">%s</h1>
                                </td>
                            </tr>

                            <tr>
                                <td style="font-size:15px; color:#333; line-height:1.7;">
                                    %s
                                </td>
                            </tr>

                            <tr>
                                <td align="center" style="padding-top:30px; padding-bottom:10px;">
                                    <hr style="border:none; border-top:1px solid #e5e7eb;">
                                    <p style="font-size:13px; color:#777;">
                                        MedicConnect Healthcare Platform<br>
                                        <a href="#" style="color:#2563eb; text-decoration:none;">www.medicconnect.com</a>
                                    </p>
                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>

        </body>
        </html>
        """.formatted(title, bodyContent);
    }

    // ---------------------------------------------------------
    //  OTP EMAILS
    // ---------------------------------------------------------
    private String generateOtpSection(String name, String otp, String purpose) {

        String issuedAt = LocalDateTime.now().format(DATE_FORMATTER);

        return """
            <p>Hello <strong>%s</strong>,</p>
            <p>Your <strong>%s OTP</strong> is shown below:</p>

            <div style="text-align:center; margin:25px 0;">
                <div style="background:#2563eb;
                            color:white;
                            font-size:28px;
                            font-weight:bold;
                            border-radius:8px;
                            padding:12px 25px;
                            display:inline-block;
                            letter-spacing:3px;">
                    %s
                </div>
            </div>

            <p style="color:#555;">Issued on: %s</p>
        """.formatted(name, purpose, otp, issuedAt);
    }

    public String generateLoginOtpEmail(String name, String otp) {
        return wrapTemplate("Login OTP", generateOtpSection(name, otp, "Login"));
    }

    public String generateEmailVerificationOtpEmail(String name, String otp) {
        return wrapTemplate("Email Verification OTP", generateOtpSection(name, otp, "Email Verification"));
    }

    // ---------------------------------------------------------
    //  REGISTRATION EMAILS
    // ---------------------------------------------------------

    public String generatePersonRegistrationSuccessEmail(
            String name, String email, String mobile, String role,
            String uuid, String orgName, LocalDateTime time) {

        String body = """
            <p>Hello <strong>%s</strong>,</p>
            <p>Your registration has been successfully completed.</p>

            <table style="width:100%%; margin:15px 0; border-collapse:collapse;">
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Mobile:</strong></td><td>%s</td></tr>
                <tr><td><strong>Role:</strong></td><td>%s</td></tr>
                <tr><td><strong>Organization:</strong></td><td>%s</td></tr>
                <tr><td><strong>User ID:</strong></td><td>%s</td></tr>
            </table>

            <p>Time: %s</p>
        """.formatted(name, email, mobile, role, orgName, uuid,
                time.format(DATE_FORMATTER));

        return wrapTemplate("Registration Successful", body);
    }


    public String generateNewUserNotificationForAdmin(
            String name, String email, String mobile, String orgName,
            List<String> roles, String userType, String id,
            String uuid, LocalDateTime time) {

        String body = """
            <p><strong>New user registration request</strong></p>

            <table style="width:100%%; margin-top:10px;">
                <tr><td><strong>Name:</strong></td><td>%s</td></tr>
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Mobile:</strong></td><td>%s</td></tr>
                <tr><td><strong>Organization:</strong></td><td>%s</td></tr>
                <tr><td><strong>Roles:</strong></td><td>%s</td></tr>
                <tr><td><strong>User Type:</strong></td><td>%s</td></tr>
                <tr><td><strong>User ID:</strong></td><td>%s</td></tr>
                <tr><td><strong>UUID:</strong></td><td>%s</td></tr>
            </table>

            <p>Time: %s</p>
        """.formatted(
                name, email, mobile, orgName,
                String.join(", ", roles), userType, id, uuid,
                time.format(DATE_FORMATTER)
        );

        return wrapTemplate("New User Registration", body);
    }


    public String generateUserRegistrationPendingEmail(
            String name, String email, String mobile, List<String> roles,
            String orgName, String id, String uuid, LocalDateTime time) {

        String body = """
            <p>Hello <strong>%s</strong>,</p>
            <p>Your registration is currently pending approval.</p>

            <table style="width:100%%; margin-top:10px;">
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Mobile:</strong></td><td>%s</td></tr>
                <tr><td><strong>Roles:</strong></td><td>%s</td></tr>
                <tr><td><strong>Organization:</strong></td><td>%s</td></tr>
                <tr><td><strong>ID:</strong></td><td>%s</td></tr>
                <tr><td><strong>UUID:</strong></td><td>%s</td></tr>
            </table>

            <p>Time: %s</p>
        """.formatted(
                name, email, mobile,
                String.join(", ", roles), orgName, id, uuid,
                time.format(DATE_FORMATTER)
        );

        return wrapTemplate("Registration Pending", body);
    }


    public String generateUserApprovedEmail(
            String name, String email, String mobile, List<String> roles,
            String orgName, String id, String uuid, LocalDateTime time) {

        String body = """
            <p>Hello <strong>%s</strong>,</p>
            <p>Your account has been approved!</p>

            <table style="width:100%%; margin-top:10px;">
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Mobile:</strong></td><td>%s</td></tr>
                <tr><td><strong>Roles:</strong></td><td>%s</td></tr>
                <tr><td><strong>Organization:</strong></td><td>%s</td></tr>
                <tr><td><strong>ID:</strong></td><td>%s</td></tr>
                <tr><td><strong>UUID:</strong></td><td>%s</td></tr>
            </table>

            <p>Approved on: %s</p>
        """.formatted(
                name, email, mobile,
                String.join(", ", roles), orgName, id, uuid,
                time.format(DATE_FORMATTER)
        );

        return wrapTemplate("Registration Approved", body);
    }


    public String generateUserRejectedEmail(
            String name, String email, String mobile, List<String> roles,
            String orgName, String id, String uuid, LocalDateTime time) {

        String body = """
            <p>Hello <strong>%s</strong>,</p>
            <p>Your registration request has been rejected.</p>

            <table style="width:100%%; margin-top:10px;">
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Mobile:</strong></td><td>%s</td></tr>
                <tr><td><strong>Roles:</strong></td><td>%s</td></tr>
                <tr><td><strong>Organization:</strong></td><td>%s</td></tr>
                <tr><td><strong>ID:</strong></td><td>%s</td></tr>
                <tr><td><strong>UUID:</strong></td><td>%s</td></tr>
            </table>

            <p>Time: %s</p>
        """.formatted(
                name, email, mobile,
                String.join(", ", roles), orgName, id, uuid,
                time.format(DATE_FORMATTER)
        );

        return wrapTemplate("Registration Rejected", body);
    }


    public String generateOrgRegistrationSuccessEmail(
            String orgName, String adminName, String email, String id) {

        String body = """
            <p>Organization registered successfully!</p>

            <table style="width:100%%; margin-top:10px;">
                <tr><td><strong>Organization:</strong></td><td>%s</td></tr>
                <tr><td><strong>Admin:</strong></td><td>%s</td></tr>
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Org ID:</strong></td><td>%s</td></tr>
            </table>
        """.formatted(orgName, adminName, email, id);

        return wrapTemplate("Organization Registered", body);
    }

    // ---------------------------------------------------------
    //  PREFILLED REGISTRATION EMAIL
    // ---------------------------------------------------------
    public String generatePreFilledRegistrationEmail(PersonDTO dto, String token) {

        String registrationLink = token; 

        String body = """
            <p>Hello <strong>%s</strong>,</p>
            <p>Please complete your registration using the link below.</p>

            <p><strong>Email:</strong> %s<br/>
               <strong>Mobile:</strong> %s<br/>
               <strong>Role(s):</strong> %s
            </p>

            <div style="text-align:center; margin:30px 0;">
                <a href="%s"
                   style="background:#2563eb;
                          color:white;
                          padding:12px 25px;
                          border-radius:8px;
                          text-decoration:none;
                          font-size:16px;">
                    Complete Registration
                </a>
            </div>
        """.formatted(
                dto.getName(),
                dto.getEmail(),
                dto.getMobile(),
                String.join(", ", dto.getRoles()),
                registrationLink
        );

        return wrapTemplate("Complete Registration", body);
    }
}
