package hanium.smath.notice.service;

import hanium.smath.notice.entity.Notice; // 수정됨
import hanium.smath.notice.dto.NoticeRequest;
import hanium.smath.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Transactional(readOnly = true)
    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    @Transactional
    public Notice create(NoticeRequest noticeRequest, String author) {
        Notice notice = new Notice();
        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
        notice.setAuthor(author); // author 설정

        return noticeRepository.save(notice);
    }


    @Transactional
    public Notice update(Long id, NoticeRequest noticeRequest) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            notice.setTitle(noticeRequest.getTitle());
            notice.setContent(noticeRequest.getContent());
            return noticeRepository.save(notice);
        } else {
            throw new RuntimeException("Notice not found with id: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            noticeRepository.delete(optionalNotice.get());
        } else {
            throw new RuntimeException("Notice not found with id: " + id);
        }
    }
}