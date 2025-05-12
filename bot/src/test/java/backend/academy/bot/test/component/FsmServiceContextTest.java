package backend.academy.bot.test.component;

import backend.academy.bot.converter.ChatContextToAddLinkRequestConverter;
import backend.academy.bot.converter.ChatContextToUpdateTagsRequestConverter;
import backend.academy.bot.fsm.CommandRegistry;
import backend.academy.bot.fsm.command.ByTagsCommand;
import backend.academy.bot.fsm.command.CancelCommand;
import backend.academy.bot.fsm.command.ChangeTagsCommand;
import backend.academy.bot.fsm.command.HelpCommand;
import backend.academy.bot.fsm.command.ListCommand;
import backend.academy.bot.fsm.command.StartCommand;
import backend.academy.bot.fsm.command.TrackCommand;
import backend.academy.bot.fsm.command.UntrackCommand;
import backend.academy.bot.fsm.highhierarchystate.Idle;
import backend.academy.bot.fsm.highhierarchystate.InCommand;
import backend.academy.bot.fsm.highhierarchystate.NotRegistered;
import backend.academy.bot.repository.CaffeineChatContextRepository;
import backend.academy.bot.scrapper.ScrapperClient;
import backend.academy.bot.service.FieldValidatorService;
import backend.academy.bot.service.FsmService;
import backend.academy.dto.validator.service.FiltersValidatorService;
import backend.academy.dto.validator.service.TagsValidatorService;
import backend.academy.dto.validator.service.UrlValidatorService;
import backend.academy.dto.validator.util.GithubUrlValidator;
import backend.academy.dto.validator.util.StackOverflowUrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
            FsmService.class,
            ScrapperClient.class,
            CaffeineChatContextRepository.class,
            ChatContextToAddLinkRequestConverter.class,
            Idle.class,
            NotRegistered.class,
            InCommand.class,
            ListCommand.class,
            StartCommand.class,
            TrackCommand.class,
            UntrackCommand.class,
            CancelCommand.class,
            ByTagsCommand.class,
            ChangeTagsCommand.class,
            HelpCommand.class,
            CommandRegistry.class,
            GithubUrlValidator.class,
            StackOverflowUrlValidator.class,
            UrlValidatorService.class,
            TagsValidatorService.class,
            FiltersValidatorService.class,
            FieldValidatorService.class,
            ChatContextToUpdateTagsRequestConverter.class
        })
@Import(CacheTestConfig.class)
public class FsmServiceContextTest {

    @Autowired
    FsmService fsmService;

    @MockitoBean
    ScrapperClient scrapperClient;
}
