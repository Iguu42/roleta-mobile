package com.example.roleta;

import static org.mockito.Mockito.verify;

import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EditRouletteFragmentTest {

    @Mock
    EditText mockEditTextTitle;

    @Mock
    EditText mockEditTextOption1;

    @Mock
    EditText mockEditTextOption2;

    @Mock
    EditText mockEditTextOption3;

    @Mock
    Button mockUpdateButton;

    @InjectMocks
    EditRouletteFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fragment.editTextTitle = mockEditTextTitle;
        fragment.editTextOption1 = mockEditTextOption1;
        fragment.editTextOption2 = mockEditTextOption2;
        fragment.editTextOption3 = mockEditTextOption3;
        fragment.updateButton = mockUpdateButton;
    }
    @Test
    public void testUpdateButtonClicked() {
        fragment.updateButton.performClick();
        verify(mockUpdateButton).performClick();
    }

}
