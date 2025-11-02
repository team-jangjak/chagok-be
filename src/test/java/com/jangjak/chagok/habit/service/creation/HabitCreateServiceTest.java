package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.dto.request.create.ModifyHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateHabitRequestDto;
import com.jangjak.chagok.habit.enums.HabitCreationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitCreateServiceTest {

    @Mock
    private HabitCreationFactory creationFactory;

    @Mock
    private HabitCreationTxService creationTxService;

    @Mock
    private HabitCreation newHabitCreation;

    @Mock
    private HabitCreation modifyHabitCreation;

    @Mock
    private HabitCreation templateHabitCreation;

    @InjectMocks
    private HabitCreateService habitCreateService;

    private TokenUserInfo userInfo;

    @BeforeEach
    void setUp() {
        userInfo = TokenUserInfo.builder()
                .id(2L)
                .role("USER")
                .build();
    }

    @Test
    void createNewHabit_delegatesToFactoryAndTxService() {
        NewHabitRequestDto request = new NewHabitRequestDto();
        Long expectedUserHabitId = 1L;

        when(creationFactory.getHabitCreation(HabitCreationType.NEW)).thenReturn(newHabitCreation);
        when(creationTxService.createHabit(newHabitCreation, userInfo, request)).thenReturn(expectedUserHabitId);

        Long result = habitCreateService.createNewHabit(userInfo, request);

        assertThat(result).isEqualTo(expectedUserHabitId);
        verify(creationFactory).getHabitCreation(HabitCreationType.NEW);
        verify(creationTxService).createHabit(newHabitCreation, userInfo, request);
        verifyNoMoreInteractions(creationFactory, creationTxService);
    }

    @Test
    void createModifyHabit_delegatesToFactoryAndTxService() {
        ModifyHabitRequestDto request = new ModifyHabitRequestDto();
        Long expectedUserHabitId = 2L;

        when(creationFactory.getHabitCreation(HabitCreationType.MODIFY)).thenReturn(modifyHabitCreation);
        when(creationTxService.createHabit(modifyHabitCreation, userInfo, request)).thenReturn(expectedUserHabitId);

        Long result = habitCreateService.createModifyHabit(userInfo, request);

        assertThat(result).isEqualTo(expectedUserHabitId);
        verify(creationFactory).getHabitCreation(HabitCreationType.MODIFY);
        verify(creationTxService).createHabit(modifyHabitCreation, userInfo, request);
        verifyNoMoreInteractions(creationFactory, creationTxService);
    }

    @Test
    void createTemplateHabit_delegatesToFactoryAndTxService() {
        TemplateHabitRequestDto request = new TemplateHabitRequestDto();
        request.setIsPublic(Boolean.TRUE);
        Long expectedUserHabitId = 3L;

        when(creationFactory.getHabitCreation(HabitCreationType.TEMPLATE)).thenReturn(templateHabitCreation);
        when(creationTxService.createHabit(templateHabitCreation, userInfo, request)).thenReturn(expectedUserHabitId);

        Long result = habitCreateService.createTemplateHabit(userInfo, request);

        assertThat(result).isEqualTo(expectedUserHabitId);
        verify(creationFactory).getHabitCreation(HabitCreationType.TEMPLATE);
        verify(creationTxService).createHabit(templateHabitCreation, userInfo, request);
        verifyNoMoreInteractions(creationFactory, creationTxService);
    }
}
