package backend.academy.bot.test.component;

import backend.academy.bot.converter.ChatContextToAddLinkRequestConverter;
import backend.academy.bot.converter.ChatContextToRemoveLinkRequestConverter;
import backend.academy.bot.fsm.state.states.Idle;
import backend.academy.bot.fsm.state.states.NotRegistered;
import backend.academy.bot.fsm.state.states.TrackAwaitFilters;
import backend.academy.bot.fsm.state.states.TrackAwaitTags;
import backend.academy.bot.fsm.state.states.TrackAwaitUrl;
import backend.academy.bot.fsm.state.states.UntrackAwaitUrl;
import backend.academy.bot.fsm.transition.command.HelpCommand;
import backend.academy.bot.fsm.transition.command.ListCommand;
import backend.academy.bot.fsm.transition.command.StartCommand;
import backend.academy.bot.fsm.transition.command.TrackCommand;
import backend.academy.bot.fsm.transition.command.UntrackCommand;
import backend.academy.bot.repository.CaffeineChatContextRepository;
import backend.academy.bot.service.FsmService;
import backend.academy.bot.service.ScrapperClient;
import backend.academy.dto.validator.UrlValidatorService;
import backend.academy.dto.validator.validators.GithubUrlValidator;
import backend.academy.dto.validator.validators.StackOverflowUrlValidator;
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
            ChatContextToRemoveLinkRequestConverter.class,
            Idle.class,
            NotRegistered.class,
            TrackAwaitUrl.class,
            TrackAwaitFilters.class,
            TrackAwaitTags.class,
            UntrackAwaitUrl.class,
            HelpCommand.class,
            ListCommand.class,
            StartCommand.class,
            TrackCommand.class,
            UntrackCommand.class,
            GithubUrlValidator.class,
            StackOverflowUrlValidator.class,
            UrlValidatorService.class
        })
@Import(CacheTestConfig.class)
public class FsmServiceContextTest {

    @Autowired
    FsmService fsmService;

    @MockitoBean
    ScrapperClient scrapperClient;
}
